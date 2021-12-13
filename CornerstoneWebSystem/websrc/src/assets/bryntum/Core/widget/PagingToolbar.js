import BryntumWidgetAdapterRegister from '../adapter/widget/util/BryntumWidgetAdapterRegister.js';
import Toolbar from './Toolbar.js';

/**
 * @module Core/widget/PagingToolbar
 */

/**
 * A special Toolbar class, which, when attached to an {@link Core.data.AjaxStore AjaxStore},
 * which has been configured to be {@link Core.data.AjaxStore#property-isPaged paged}, controls
 * the loading of that store to page through the data set.
 *
 * ```javascript
 * new Panel({
 *      layout : 'fit',
 *      items  : [
 *          myGrid
 *      ],
 *      bbar : {
 *          type  : 'pagingtoolbar',
 *          store : myGrid.store
 *      }
 * });
 * ```
 *
 * @extends Core/widget/Toolbar
 * @classType toolbar
 */
export default class PagingToolbar extends Toolbar {
    static get $name() {
        return 'PagingToolbar';
    }

    static get defaultConfig() {
        return {
            /**
             * The {@link Core.data.AjaxStore AjaxStore} that this PagingToolbar is to control.
             * @config {Core.data.AjaxStore}
             */
            store : null,

            localeClass : this,

            namedItems : {

                firstPageButton : {
                    onClick : 'up.onFirstPageClick',
                    icon    : 'b-fa-angle-double-left'
                },
                previousPageButton : {
                    onClick : 'up.onPreviousPageClick',
                    icon    : 'b-fa-angle-left'
                },
                pageNumber : {
                    type     : 'numberfield',
                    label    : 'L{page}',
                    min      : 1,
                    max      : 1,
                    triggers : null,
                    onChange : 'up.onPageNumberChange'
                },
                pageCount : {
                    type : 'widget',
                    cls  : 'b-pagecount b-toolbar-text'
                },
                nextPageButton : {
                    onClick : 'up.onNextPageClick',
                    icon    : 'b-fa-angle-right'
                },
                lastPageButton : {
                    onClick : 'up.onLastPageClick',
                    icon    : 'b-fa-angle-double-right'
                },
                reloadButton : {
                    onClick : 'up.onReloadClick',
                    icon    : 'b-fa-redo'
                },
                dataSummary : {
                    type : 'widget',
                    cls  : 'b-toolbar-text'
                }
            },

            items : {
                firstPageButton    : true,
                previousPageButton : true,
                pageNumber         : true,
                pageCount          : true,
                nextPageButton     : true,
                lastPageButton     : true,
                sep                : '|',
                reloadButton       : true,
                spacer             : '->',
                dataSummary        : true
            }
        };
    }

    set store(store) {
        const
            me = this,
            listener = {
                beforerequest : 'onStoreBeforeRequest',
                afterrequest  : 'onStoreChange',
                change        : 'onStoreChange',
                thisObj       : me
            };

        if (me.store) {
            me.store.un(listener);
        }

        me._store = store;
        if (store) {
            store.on(listener);
            if (store.isLoading) {
                me.onStoreBeforeRequest();
            }
        }
    }

    get store() {
        return this._store;
    }

    onStoreBeforeRequest() {
        this.eachWidget(w => w.disable());
    }

    updateLocalization() {
        const
            me = this,
            { reloadButton,  firstPageButton, previousPageButton, nextPageButton, lastPageButton } = me.widgetMap;

        firstPageButton.tooltip = me.L('firstPage');
        previousPageButton.tooltip = me.L('prevPage');
        nextPageButton.tooltip = me.L('nextPage');
        lastPageButton.tooltip = me.L('lastPage');
        reloadButton.tooltip = me.L('reload');

        me.updateSummary();

        super.updateLocalization();
    }

    updateSummary() {
        const
            me                         = this,
            { pageCount, dataSummary } = me.widgetMap;

        let count, lastPage, start, end, allCount;

        count = lastPage = start = end = allCount = 0;

        if (me.store) {
            const
                { store } = me,
                { pageSize, currentPage } = store;

            count = store.count;
            lastPage = store.lastPage;
            allCount = store.allCount;

            start = Math.max(0, (currentPage - 1) * pageSize + 1);
            end = Math.min(allCount, start + pageSize - 1);
        }

        pageCount.html = me.L('pageCountTemplate')({ lastPage });
        dataSummary.html = count ? me.L('summaryTemplate')({ start, end, allCount }) : me.L('noRecords');
    }

    onStoreChange() {
        const
            me = this,
            { widgetMap, store } = me,
            { count, lastPage, currentPage } = store,
            { pageNumber, pageCount, firstPageButton, previousPageButton, nextPageButton, lastPageButton, dataSummary } = widgetMap;

        me.eachWidget(w => w.enable());

        pageNumber.value = currentPage;
        pageNumber.max = lastPage;

        dataSummary.disabled = pageNumber.disabled = pageCount.disabled = !count;
        firstPageButton.disabled = previousPageButton.disabled = currentPage <= 1 || !count;
        nextPageButton.disabled  = lastPageButton.disabled = currentPage >= lastPage || !count;

        me.updateSummary();
    }

    onPageNumberChange({ value }) {
        if (this.store.currentPage !== value) {
            this.store.loadPage(value);
        }
    }

    onFirstPageClick() {
        this.store.loadPage(1);
    }

    onPreviousPageClick() {
        this.store.previousPage();
    }

    onNextPageClick() {
        this.store.nextPage();
    }

    onLastPageClick() {
        this.store.loadPage(this.store.lastPage);
    }

    onReloadClick() {
        this.store.loadPage(this.store.currentPage);
    }
}

BryntumWidgetAdapterRegister.register('pagingtoolbar', PagingToolbar);
