import LocaleManager from '../../Core/localization/LocaleManager.js';
import coreLocale from '../../Core/localization/SvSE.js';

// Extends locale from Core
const locale = {

    //region Columns

    TemplateColumn : {
        noTemplate : 'TemplateColumn kräver en template',
        noFunction : 'TemplateColumn.template måste vara en funktion'
    },

    ColumnStore : {
        columnTypeNotFound : data => `Kolumntypen '${data.type}' är inte registrerad`
    },

    //endregion

    //region Features

    ColumnPicker : {
        columnsMenu     : 'Kolumner',
        hideColumn      : 'Dölj kolumn',
        hideColumnShort : 'Dölj'
    },

    Filter : {
        applyFilter  : 'Använd filter',
        editFilter   : 'Redigera filter',
        filter       : 'Filter',
        on           : 'På',
        before       : 'Före',
        after        : 'Efter',
        equals       : 'Lika med',
        lessThan     : 'Mindre än',
        moreThan     : 'Större än',
        removeFilter : 'Ta bort filter'
    },

    FilterBar : {
        enableFilterBar  : 'Visa filterrad',
        disableFilterBar : 'Dölj filterrad'
    },

    Group : {
        groupAscending       : 'Gruppera stigande',
        groupDescending      : 'Gruppera fallande',
        groupAscendingShort  : 'Stigande',
        groupDescendingShort : 'Fallande',
        stopGrouping         : 'Sluta gruppera',
        stopGroupingShort    : 'Sluta'
    },

    Search : {
        searchForValue : 'Sök efter värde'
    },

    Sort : {
        sortAscending          : 'Sortera stigande',
        sortDescending         : 'Sortera fallande',
        multiSort              : 'Multisortering',
        addSortAscending       : 'Lägg till stigande',
        addSortDescending      : 'Lägg till fallande',
        toggleSortAscending    : 'Ändra till stigande',
        toggleSortDescending   : 'Ändra till fallande',
        removeSorter           : 'Ta bort sorterare',
        sortAscendingShort     : 'Stigande',
        sortDescendingShort    : 'Fallande',
        removeSorterShort      : 'Ta bort',
        addSortAscendingShort  : '+ Stigande',
        addSortDescendingShort : '+ Fallande'
    },

    Tree : {
        noTreeColumn : 'För att använda featuren tree måste en kolumn vara konfigurerad med tree: true'
    },

    //endregion

    //region Grid

    Grid : {
        featureNotFound          : data => `Featuren '${data}' är inte tillgänglig, kontrollera att den är importerad`,
        invalidFeatureNameFormat : data => `Ogiltigt funktionsnamn '${data}' måste börja med en liten bokstav`,
        removeRow                : 'Ta bort rad',
        removeRows               : 'Ta bort rader',
        loadFailedMessage        : 'Ett fel har uppstått, vänligen försök igen.',
        moveColumnLeft           : 'Flytta till vänstra sektionen',
        moveColumnRight          : 'Flytta till högra sektionen'
    },

    GridBase : {
        loadMask : 'Laddar...',
        noRows   : 'Inga rader att visa'
    },

    //endregion

    //region Export
    
    PdfExport : {
        'Waiting for response from server...' : 'Väntar på svar från servern...'
    },
    
    ExportDialog : {
        width          : '40em',
        labelWidth     : '13em',
        exportSettings : 'Exportera inställningar',
        export         : 'Exportera',
        exporterType   : 'Styra sidbrytningarna',
        cancel         : 'Avbryt',
        fileFormat     : 'Filformat',
        rows           : 'Кader',
        alignRows      : 'Anpassa raderna',
        columns        : 'Kolumner',
        paperFormat    : 'Pappersformat',
        orientation    : 'Orientering'
    },
    
    ExportRowsCombo : {
        all     : 'Alla rader',
        visible : 'Synliga rader'
    },
    
    ExportOrientationCombo : {
        portrait  : 'Stående',
        landscape : 'Liggande'
    },
    
    SinglePageExporter : {
        singlepage : 'En sida'
    },
    
    MultiPageExporter : {
        multipage     : 'Flera sidor',
        exportingPage : ({ currentPage, totalPages }) => `Exporterar sidan ${currentPage}/${totalPages}`
    }
    
    //endregion
};

// cannot use Object.assign above in IE11. also dont want to have polyfill in locale
for (let i in coreLocale) {
    locale[i] = coreLocale[i];
}

export default locale;

LocaleManager.registerLocale('SvSE', { desc : 'Svenska', locale : locale });
