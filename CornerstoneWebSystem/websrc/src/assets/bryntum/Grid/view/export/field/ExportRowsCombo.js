import BryntumWidgetAdapterRegister from '../../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import { RowsRange } from '../../../feature/export/Utils.js';
import LocalizableCombo from './LocalizableCombo.js';

export default class ExportRowsCombo extends LocalizableCombo {
    static get $name() {
        return 'ExportRowsCombo';
    }
    
    static get defaultConfig() {
        return {
            editable : false
        };
    }
    
    buildLocalizedItems() {
        const me = this;
        
        return [
            { id : RowsRange.all, text : me.L(RowsRange.all) },
            { id : RowsRange.visible, text : me.L(RowsRange.visible) }
        ];
    }
}

BryntumWidgetAdapterRegister.register(ExportRowsCombo.$name, ExportRowsCombo);
