import LocaleManager from '../../Core/localization/LocaleManager.js';
import coreLocale from '../../Core/localization/Nl.js';

// Extends locale from grid
const locale = {

    //region Columns

    TemplateColumn : {
        noTemplate : 'TemplateColumn heeft een template nodig',
        noFunction : 'TemplateColumn.template moet een functie zijn'
    },

    ColumnStore : {
        columnTypeNotFound : data => `Kolom type '${data.type}' is niet geregistreerd`
    },

    //endregion

    //region Features

    ColumnPicker : {
        columnsMenu     : 'Kolommen',
        hideColumn      : 'Verberg Kolom',
        hideColumnShort : 'Verberg'
    },

    Filter : {
        applyFilter  : 'Pas filter toe',
        filter       : 'Filter',
        editFilter   : 'Wijzig filter',
        on           : 'Aan',
        before       : 'Voor',
        after        : 'Na',
        equals       : 'Is gelijk',
        lessThan     : 'Minder dan',
        moreThan     : 'Meer dan',
        removeFilter : 'Verwijder filter'
    },

    FilterBar : {
        enableFilterBar  : 'Maak filterbalk zichtbaar',
        disableFilterBar : 'Verberg filterbalk'
    },

    Group : {
        groupAscending       : 'Groepeer oplopend',
        groupDescending      : 'Groepeer aflopend',
        groupAscendingShort  : 'Oplopend',
        groupDescendingShort : 'Aflopend',
        stopGrouping         : 'Maak groepering ongedaan',
        stopGroupingShort    : 'Maak ongedaan'
    },

    Search : {
        searchForValue : 'Zoek op term'
    },

    Sort : {
        'sortAscending'          : 'Sorteer oplopend',
        'sortDescending'         : 'Sorteer aflopend',
        'multiSort'              : 'Meerdere sorteringen',
        'removeSorter'           : 'Verwijder sortering',
        'addSortAscending'       : 'Voeg oplopende sortering toe',
        'addSortDescending'      : 'Voeg aflopende sortering toe',
        'toggleSortAscending'    : 'Sorteer oplopend',
        'toggleSortDescending'   : 'Sorteer aflopend',
        'sortAscendingShort'     : 'Oplopend',
        'sortDescendingShort'    : 'Aflopend',
        'removeSorterShort'      : 'Verwijder',
        'addSortAscendingShort'  : '+ Oplopend',
        'addSortDescendingShort' : '+ Aflopend'
    },

    Tree : {
        noTreeColumn : 'Om de boomstructuur (tree) eigenschap te kunnen gebruiken zet, tree: true'
    },

    //endregion

    //region Grid

    Grid : {
        featureNotFound          : data => `Eigenschap '${data}' is niet beschikbaar, controleer of u de optie geimporteerd heeft`,
        invalidFeatureNameFormat : data => `Ongeldige functienaam '${data}', moet beginnen met een kleine letter`,
        removeRow                : 'Verwijder rij',
        removeRows               : 'Verwijder rijen',
        loadFailedMessage        : 'Laden mislukt.',
        moveColumnLeft           : 'Plaats naar het linker kader',
        moveColumnRight          : 'Plaats naar het rechter kader'
    },

    GridBase : {
        loadMask : 'Laden...',
        noRows   : 'Geen rijen om weer te geven'
    },

    //endregion

    //region Export
    
    PdfExport : {
        'Waiting for response from server...' : 'Wachten op antwoord van server...'
    },
    
    ExportDialog : {
        width          : '40em',
        labelWidth     : '12em',
        exportSettings : 'Instellingen exporteren',
        export         : 'Exporteren',
        exporterType   : 'Paginering beheren',
        cancel         : 'Annuleren',
        fileFormat     : 'Bestandsformaat',
        rows           : 'Rijen',
        alignRows      : 'Rijen uitlijnen',
        columns        : 'Columns',
        paperFormat    : 'Papier formaat',
        orientation    : 'Oriëntatatie'
    },
    
    ExportRowsCombo : {
        all     : 'Alle rijen',
        visible : 'Zichtbare rijen'
    },
    
    ExportOrientationCombo : {
        portrait  : 'Staand',
        landscape : 'Liggend'
    },
    
    SinglePageExporter : {
        singlepage : 'Enkele pagina'
    },
    
    MultiPageExporter : {
        multipage     : 'Meerdere pagina\'s',
        exportingPage : ({ currentPage, totalPages }) => `Exporteren van de pagina ${currentPage}/${totalPages}`
    }
    
    //endregion
};

// cannot use Object.assign above in IE11. also dont want to have polyfill in locale
for (let i in coreLocale) {
    locale[i] = coreLocale[i];
}

export default locale;

LocaleManager.registerLocale('Nl', { desc : 'Nederlands', locale : locale });
