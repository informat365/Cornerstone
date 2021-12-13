import Popup from '../../../Core/widget/Popup.js';
import LocaleManager from '../../../Core/localization/LocaleManager.js';
import './field/ExportRowsCombo.js';
import './field/ExportOrientationCombo.js';
import './field/LocalizableCombo.js';
import { FileFormat, Orientation, PaperFormat, RowsRange } from '../../feature/export/Utils.js';
import Checkbox from '../../../Core/widget/Checkbox.js';
import Field from '../../../Core/widget/Field.js';

/**
 * @module Grid/view/export/ExportDialog
 */

/**
 * Dialog window which allows to pick export options.
 *
 * ```
 * grid = new Grid({
 *     features : {
 *         pdfExport : { exportServer : '...' }
 *     }
 * });
 *
 * grid.features.pdfExport.showExportDialog();
 * ```
 *
 * @extends Core/widget/Popup
 */
export default class ExportDialog extends Popup {
    static get $name() {
        return 'ExportDialog';
    }
    
    static get defaultConfig() {
        return {
            autoShow  : false,
            autoClose : false,
            closable  : true,
            centered  : true,
    
            /**
             * Grid instance to build export dialog for
             */
            client : null,
    
            /**
             * Set to `false` to allow using PNG + Multipage config in export dialog
             */
            hidePNGMultipageOption : true,

            title : 'L{exportSettings}',
            
            bbar : [
                {
                    type        : 'button',
                    ref         : 'exportButton',
                    color       : 'b-green',
                    localeClass : this,
                    text        : 'L{export}'
                },
                {
                    type        : 'button',
                    ref         : 'cancelButton',
                    color       : 'b-gray',
                    localeClass : this,
                    text        : 'L{cancel}'
                }
            ]
        };
    }
    
    buildDialogItems(config) {
        const
            me                    = this,
            { exporters, client } = config,
            labelWidth            = me.L('labelWidth');
        
        me.columnsStore = client.columns.chain(record => record.isLeaf);
    
        function buildComboItems(obj, fn = x => x) {
            return Object.keys(obj).map(key => ({ id : key, text : fn(key) }));
        }
    
        return [
            {
                labelWidth,
                type         : 'combo',
                ref          : 'columnsField',
                label        : 'L{columns}',
                localeClass  : this,
                store        : me.columnsStore,
                value        : me.columnsStore.allRecords,
                valueField   : 'id',
                displayField : 'text',
                multiSelect  : true
            },
            {
                labelWidth,
                type        : 'exportrowscombo',
                ref         : 'rowsRangeField',
                label       : 'L{rows}',
                localeClass : this,
                value       : RowsRange.all
            },
            {
                labelWidth,
                type                : 'localizablecombo',
                ref                 : 'exporterTypeField',
                label               : 'L{exporterType}',
                localeClass         : this,
                editable            : false,
                value               : exporters[0].type,
                buildLocalizedItems : () => exporters.map(exporter => ({ id : exporter.type, text : exporter.title })),
                onChange({ value }) {
                    this.owner.widgetMap.alignRowsField.hidden = value !== 'multipage';
                }
            },
            {
                labelWidth,
                type        : 'checkbox',
                ref         : 'alignRowsField',
                label       : 'L{alignRows}',
                localeClass : this,
                checked     : true,
                hidden      : exporters[0].type !== 'multipage'
            },
            {
                labelWidth,
                type        : 'combo',
                ref         : 'fileFormatField',
                label       : 'L{fileFormat}',
                localeClass : this,
                editable    : false,
                value       : FileFormat.pdf,
                items       : buildComboItems(FileFormat, value => value.toUpperCase()),
                onChange({ value, oldValue }) {
                    if (me.hidePNGMultipageOption) {
                        const
                            exporterField = me.widgetMap.exporterTypeField,
                            exporter      = exporterField.store.find(r => r.id === 'singlepage');
                        
                        if (value === FileFormat.png && exporter) {
                            this._previousDisabled = exporterField.disabled;
                            exporterField.disabled = true;
                            
                            this._previousValue = exporterField.value;
                            exporterField.value = 'singlepage';
                        }
                        else if (oldValue === FileFormat.png && this._previousValue) {
                            exporterField.disabled = this._previousDisabled;
                            exporterField.value    = this._previousValue;
                        }
                    }
                }
            },
            {
                labelWidth,
                type        : 'combo',
                ref         : 'paperFormatField',
                label       : 'L{paperFormat}',
                localeClass : this,
                editable    : false,
                value       : 'A4',
                items       : buildComboItems(PaperFormat)
            },
            {
                labelWidth,
                type        : 'exportorientationcombo',
                ref         : 'orientationField',
                label       : 'L{orientation}',
                localeClass : this,
                value       : Orientation.portrait
            }
        ];
    }
    
    construct(config = {}) {
        const
            me         = this,
            { client } = config;
        
        if (!client) {
            throw new Error('`client` config is required');
        }
    
        const items = me.buildDialogItems(config);
        
        if (config.items) {
            if (Array.isArray(config.items)) {
                config.items.push(...items);
            }
            else {
                items.forEach(item => {
                    config.items[item.ref] = item;
                    delete item.ref;
                });
            }
        }
        else {
            config.items = items;
        }
        
        config.width = config.width || me.L('width');
        
        super.construct(config);
        
        me.widgetMap.exportButton.on('click', me.onExportClick, me);
        me.widgetMap.cancelButton.on('click', me.onCancelClick, me);
    
        LocaleManager.on({
            locale  : 'onLocaleChange',
            prio    : -1,
            thisObj : me
        });
    }
    
    onLocaleChange() {
        const
            labelWidth = this.L('labelWidth');
        
        this.width = this.L('width');
        
        this.eachWidget(widget => {
            if (widget instanceof Field) {
                widget.labelWidth = labelWidth;
            }
        });
    }
    
    onExportClick() {
        const values = this.values;
    
        /**
         * Fires when export button is clicked
         * @event export
         * @param {Object} values Object containing config for {@link Grid.feature.export.PdfExport#function-export export()} method
         * @group Export
         */
        this.trigger('export', { values });
    }
    
    onCancelClick() {
        /**
         * Fires when cancel button is clicked. Popup will hide itself.
         * @event cancel
         * @group Export
         */
        this.trigger('cancel');
        this.hide();
    }
    
    get values() {
        const
            fieldRe = /field/i,
            result = {};
        
        this.eachWidget(widget => {
            if (fieldRe.test(widget.ref)) {
                result[widget.ref.replace(fieldRe, '')] = widget instanceof Checkbox ? widget.checked : widget.value;
            }
        });
        
        return result;
    }
}
