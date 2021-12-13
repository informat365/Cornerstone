import Column from './Column.js';
import ColumnStore from '../data/ColumnStore.js';

/**
 * @module Grid/column/TreeColumn
 */

let currentParentHasIcon = false;

/**
 * A column that displays a tree structure when using the {@link Grid.feature.Tree tree} feature.
 *
 * When the TreeColumn renders its cells, it will look for two special fields {@link Grid.data.GridRowModel#field-href} and {@link Grid.data.GridRowModel#field-target}. Specifying `href` will produce a link for the TreeNode, and `target` will
 * have the same meaning as in an A tag:
 *
 * ```javascript
 * {
 *    id        : 1,
 *    name      : 'Some external link'
 *    href      : '//www.website.com",
 *    target    : '_blank"
 * }
 * ```
 *
 * @example
 * new TreeGrid({
 *     appendTo : document.body,
 *
 *     columns : [
 *          { type: 'tree', field: 'name' }
 *     ]
 * });
 *
 * @classType tree
 * @extends Grid/column/Column
 * @externalexample column/TreeColumn.js
 */
export default class TreeColumn extends Column {
    static get defaults() {
        return {
            tree     : true,
            hideable : false,
            minWidth : 150
        };
    }

    static get fields() {
        return [
            /**
             * The icon to use for the collapse icon in collapsed state
             * @config {String} expandIconCls
             */
            { name : 'expandIconCls', defaultValue : 'b-icon b-icon-tree-expand' },

            /**
             * The icon to use for the collapse icon in expanded state
             * @config {String} collapseIconCls
             */
            { name : 'collapseIconCls', defaultValue : 'b-icon b-icon-tree-collapse' },

            /**
             * The icon to use for the collapse icon in expanded state
             * @config {String} collapsedFolderIconCls
             */
            //{ name : 'collapsedFolderIconCls', defaultValue : 'b-icon b-icon-tree-folder' },
            { name : 'collapsedFolderIconCls' },

            /**
             * The icon to use for the collapse icon in expanded state
             * @config {String} expandedFolderIconCls
             */
            //{ name : 'expandedFolderIconCls', defaultValue : 'b-icon b-icon-tree-folder-open' },
            { name : 'expandedFolderIconCls' },

            /**
             * The icon to use for the leaf nodes in the tree
             * @config {String} leafIconCls
             */
            { name : 'leafIconCls', defaultValue : 'b-icon b-icon-tree-leaf' },

            { name : 'editTargetSelector', defaultValue : '.b-tree-cell-value' }
        ];
    }

    static get type() {
        return 'tree';
    }

    constructor(config, store) {
        super(...arguments);

        const me = this;

        me.internalCellCls = 'b-tree-cell';

        // We handle htmlEncoding in this class rather than relying on the generic Row DOM manipulation
        // since this class requires quite a lot of DOM infrastructure around the actual rendered content
        me.shouldHtmlEncode = me.htmlEncode;
        me.tempDiv = document.createElement('div');
        me.setData('htmlEncode', false);

        // add tree renderer (which calls original renderer internally)
        if (me.renderer) {
            me.originalRenderer = me.renderer;
        }
        me.renderer = me.treeRenderer.bind(me);
    }

    /**
     * A column renderer that is automatically added to the column with { tree: true }. It adds padding and node icons
     * to the cell to make the grid appear to be a tree. The original renderer is called in the process.
     * @private
     */
    treeRenderer(renderData) {
        const
            me                                     = this,
            { cellElement, row, record, isExport } = renderData,
            gridMeta                               = record.instanceMeta(renderData.grid.store),
            tag                                    = record.href ? 'a' : 'div';
    
        let value = renderData.value,
            html  = '',
            cls   = '',
            iconCls, result;
    
        if (me.originalRenderer) {
            const rendererHtml = me.originalRenderer(renderData);
            value = rendererHtml === false ? cellElement.innerHTML : rendererHtml;
        }
    
        if (!isExport) {
            if (!record.isLeaf) {
                row.addCls && row.addCls('b-tree-parent-row');
                cellElement.classList.add('b-tree-parent-cell');
            
                // Spinner while loading children, added to row in Tree#toggleCollapse but needs to be readded if row is
                // rerendered during load
                if (gridMeta.isLoadingChildren) {
                    row.addCls('b-loading-children');
                }
            
                // TODO remove IE 11 is no longer supported (doesnt support this)
                //cellElement.classList.toggle('b-tree-collapsed', record.meta.collapsed === true);
            
                html += `<div class="b-tree-expander ${gridMeta.collapsed ? (me.expandIconCls + ' b-tree-collapsed') : (me.collapseIconCls + ' b-tree-expanded')}"></div>`;
            
                // Allow user to customize tree icon or opt out entirely
                currentParentHasIcon = iconCls = renderData.iconCls || record.iconCls || (gridMeta.collapsed ? me.collapsedFolderIconCls : me.expandedFolderIconCls);
                if (iconCls) {
                    html += `<div class="b-tree-icon ${iconCls}"></div>`;
                }
            }
            else {
                // TODO: Cleanup for reusing dom nodes should be done elsewhere, also cleanup selection
                cellElement.classList.add('b-tree-leaf-cell');
            
                // Allow user to customize tree icon or opt out entirely
                iconCls = renderData.iconCls || record.iconCls || me.leafIconCls;
                if (iconCls) {
                    cls += iconCls;
                }
            }
        
            value = value != null ? value : '';
        
            if (me.shouldHtmlEncode) {
                me.tempDiv.innerText = value;
                value = me.tempDiv.innerHTML;
            }
        
            html += `<div class="b-tree-cell-value">${value}</div>`;
        
            // TODO: make size configurable
            const padding = (record.childLevel * 1.7 + (record.isLeaf ? currentParentHasIcon ? 1.8 : iconCls ? 0.4 : 0.3 : 0));
        
            result = `<${tag} ${record.href ? `href="${record.href}"` : ''} ${tag === 'a' && record.target ? `target="${record.target}"` : ''} class="b-tree-cell-inner ${cls}" style="padding-left:${padding}em">${html}</${tag}>`;
        }
        else {
            result = value != null ? value : '';
        }
        
        return result;
    }
}

ColumnStore.registerColumnType(TreeColumn, true);
TreeColumn.exposeProperties();
