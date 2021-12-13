import LocaleManager from '../../Core/localization/LocaleManager.js';
import coreLocale from '../../Core/localization/En.js';

// Extends locale from Core
const locale = {

    //region Columns

    TemplateColumn : {
        noTemplate : 'TemplateColumn needs a template',
        noFunction : 'TemplateColumn.template must be a function'
    },

    ColumnStore : {
        columnTypeNotFound : data => `Column type '${data.type}' not registered`
    },

    //endregion

    //region Features

    ColumnPicker : {
        columnsMenu     : 'Columns',
        hideColumn      : 'Hide column',
        hideColumnShort : 'Hide'
    },

    Filter : {
        applyFilter  : 'Apply filter',
        filter       : 'Filter',
        editFilter   : 'Edit filter',
        on           : 'On',
        before       : 'Before',
        after        : 'After',
        equals       : 'Equals',
        lessThan     : 'Less than',
        moreThan     : 'More than',
        removeFilter : 'Remove filter'
    },

    FilterBar : {
        enableFilterBar  : 'Show filter bar',
        disableFilterBar : 'Hide filter bar'
    },

    Group : {
        groupAscending       : 'Group ascending',
        groupDescending      : 'Group descending',
        groupAscendingShort  : 'Ascending',
        groupDescendingShort : 'Descending',
        stopGrouping         : 'Stop grouping',
        stopGroupingShort    : 'Stop'
    },

    Search : {
        searchForValue : 'Search for value'
    },

    Sort : {
        'sortAscending'          : 'Sort ascending',
        'sortDescending'         : 'Sort descending',
        'multiSort'              : 'Multi sort',
        'removeSorter'           : 'Remove sorter',
        'addSortAscending'       : 'Add ascending sorter',
        'addSortDescending'      : 'Add descending sorter',
        'toggleSortAscending'    : 'Change to ascending',
        'toggleSortDescending'   : 'Change to descending',
        'sortAscendingShort'     : 'Ascending',
        'sortDescendingShort'    : 'Descending',
        'removeSorterShort'      : 'Remove',
        'addSortAscendingShort'  : '+ Ascending',
        'addSortDescendingShort' : '+ Descending'
    },

    Tree : {
        noTreeColumn : 'To use the tree feature one column must be configured with tree: true'
    },

    //endregion

    //region Grid

    Grid : {
        featureNotFound          : data => `Feature '${data}' not available, make sure you have imported it`,
        invalidFeatureNameFormat : data => `Invalid feature name '${data}', must start with a lowercase letter`,
        removeRow                : 'Delete record',
        removeRows               : 'Delete records',
        loadFailedMessage        : 'Data loading failed.',
        moveColumnLeft           : 'Move to left section',
        moveColumnRight          : 'Move to right section'
    },

    GridBase : {
        loadMask : 'Loading...',
        noRows   : 'No records to display'
    },

    //endregion
    
    //region Export
    
    PdfExport : {
        'Waiting for response from server...' : 'Waiting for response from server...'
    },
    
    ExportDialog : {
        width          : '40em',
        labelWidth     : '12em',
        exportSettings : 'Export settings',
        export         : 'Export',
        exporterType   : 'Control pagination',
        cancel         : 'Cancel',
        fileFormat     : 'File format',
        rows           : 'Rows',
        alignRows      : 'Align rows',
        columns        : 'Columns',
        paperFormat    : 'Paper format',
        orientation    : 'Orientation'
    },
    
    ExportRowsCombo : {
        all     : 'All rows',
        visible : 'Visible rows'
    },
    
    ExportOrientationCombo : {
        portrait  : 'Portrait',
        landscape : 'Landscape'
    },
    
    SinglePageExporter : {
        singlepage : 'Single page'
    },
    
    MultiPageExporter : {
        multipage     : 'Multiple pages',
        exportingPage : ({ currentPage, totalPages }) => `Exporting the page ${currentPage}/${totalPages}`
    }
    
    //endregion
};

// Cannot use Object.assign above in IE11. also dont want to have polyfill in locale
for (let i in coreLocale) {
    locale[i] = coreLocale[i];
}

export default locale;

LocaleManager.registerLocale('En', { desc : 'English', locale : locale });
