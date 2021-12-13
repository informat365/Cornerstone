import LocaleManager from '../../Core/localization/LocaleManager.js';
import coreLocale from '../../Core/localization/Fi.js';

// Extends locale from grid
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
        'Waiting for response from server...' : 'Odotetaan vastausta palvelimelta...'
    },
    
    ExportDialog : {
        width          : '40em',
        labelWidth     : '12em',
        exportSettings : 'Vie asetukset',
        export         : 'Viedä',
        exporterType   : 'Hallitse sivutusta',
        cancel         : 'Peruuttaa',
        fileFormat     : 'Tiedosto muoto',
        rows           : 'Riviä',
        alignRows      : 'Kohdista rivit',
        columns        : 'Pylväät',
        paperFormat    : 'Paperimuoto',
        orientation    : 'Suuntautuminen'
    },
    
    ExportRowsCombo : {
        all     : 'Kaikki rivit',
        visible : 'Näkyvät rivit'
    },
    
    ExportOrientationCombo : {
        portrait  : 'Muotokuva',
        landscape : 'Maisema'
    },
    
    SinglePageExporter : {
        singlepage : 'Yksi sivu'
    },
    
    MultiPageExporter : {
        multipage     : 'Useita sivuja',
        exportingPage : ({ currentPage, totalPages }) => `Sivun vieminen ${currentPage}/${totalPages}`
    }
    
    //endregion
};

// cannot use Object.assign above in IE11. also dont want to have polyfill in locale
for (let i in coreLocale) {
    locale[i] = coreLocale[i];
}

export default locale;

LocaleManager.registerLocale('Fi', { desc : 'Soumi', locale : locale });
