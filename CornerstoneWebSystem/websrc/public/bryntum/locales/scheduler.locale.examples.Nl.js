/*

Bryntum Scheduler 3.0.0
Copyright(c) 2019 Bryntum AB
https://bryntum.com/contact
https://bryntum.com/license

*/
!function(e,n){"object"==typeof exports&&"object"==typeof module?module.exports=n():"function"==typeof define&&define.amd?define("examples.Nl",[],n):"object"==typeof exports?exports["examples.Nl"]=n():(e.bryntum=e.bryntum||{},e.bryntum.locales=e.bryntum.locales||{},e.bryntum.locales["examples.Nl"]=n())}(window,(function(){return function(e){var n={};function t(a){if(n[a])return n[a].exports;var r=n[a]={i:a,l:!1,exports:{}};return e[a].call(r.exports,r,r.exports,t),r.l=!0,r.exports}return t.m=e,t.c=n,t.d=function(e,n,a){t.o(e,n)||Object.defineProperty(e,n,{enumerable:!0,get:a})},t.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},t.t=function(e,n){if(1&n&&(e=t(e)),8&n)return e;if(4&n&&"object"==typeof e&&e&&e.__esModule)return e;var a=Object.create(null);if(t.r(a),Object.defineProperty(a,"default",{enumerable:!0,value:e}),2&n&&"string"!=typeof e)for(var r in e)t.d(a,r,function(n){return e[n]}.bind(null,r));return a},t.n=function(e){var n=e&&e.__esModule?function(){return e.default}:function(){return e};return t.d(n,"a",n),n},t.o=function(e,n){return Object.prototype.hasOwnProperty.call(e,n)},t.p="",t(t.s=3)}([function(e,n,t){"use strict";t.r(n);var a={localeName:"Nl",localeDesc:"Nederlands",Object:{Yes:"Ja",No:"Nee",Cancel:"Annuleren"},InstancePlugin:{fnMissing:function(e){return"Het lukt niet fn ".concat(e.plugIntoName,"#").concat(e.fnName," te schakelen, de plugin fn ").concat(e.pluginName,"#").concat(e.fnName," bestaat niet")},overrideFnMissing:function(e){return"Het lukt niet fn te overerven ".concat(e.plugIntoName,"#").concat(e.fnName,", de plugin fn ").concat(e.pluginName,"#").concat(e.fnName," bestaat niet")}},Field:{invalidValue:"Ongeldige veldwaarde",minimumValueViolation:"Minimale waarde schending",maximumValueViolation:"Maximale waarde schending",fieldRequired:"Dit veld is verplicht",validateFilter:"Waarde moet worden geselecteerd in de lijst"},DateField:{invalidDate:"Ongeldige datuminvoer"},TimeField:{invalidTime:"Ongeldige tijdsinvoer"},DateHelper:{locale:"nl",shortWeek:"w",shortQuarter:"kw",week:"Week",weekStartDay:1,unitNames:[{single:"ms",plural:"ms",abbrev:"ms"},{single:"seconde",plural:"seconden",abbrev:"s"},{single:"minuut",plural:"minuten",abbrev:"m"},{single:"uur",plural:"uren",abbrev:"u"},{single:"dag",plural:"dagen",abbrev:"d"},{single:"week",plural:"weken",abbrev:"w"},{single:"maand",plural:"maanden",abbrev:"ma"},{single:"kwartaal",plural:"kwartalen",abbrev:"kw"},{single:"jaar",plural:"jaren",abbrev:"j"}],unitAbbreviations:[["mil"],["s","sec"],["m","min"],["u"],["d"],["w","wk"],["ma","mnd","m"],["k","kwar","kwt","kw"],["j","jr"]],parsers:{L:"DD-MM-YYYY",LT:"HH:mm"},ordinalSuffix:function(e){return e}},PagingToolbar:{firstPage:"Ga naar de eerste pagina",prevPage:"Ga naar de vorige pagina",page:"Pagina",nextPage:"Ga naar de volgende pagina",lastPage:"Ga naar de laatste pagina",reload:"Laad huidige pagina opnieuw",noRecords:"Geen rijen om weer te geven",pageCountTemplate:function(e){return"van ".concat(e.lastPage)},summaryTemplate:function(e){return"Records ".concat(e.start," - ").concat(e.end," van ").concat(e.allCount," worden weergegeven")}},List:{loading:"Laden..."}},r={TemplateColumn:{noTemplate:"TemplateColumn heeft een template nodig",noFunction:"TemplateColumn.template moet een functie zijn"},ColumnStore:{columnTypeNotFound:function(e){return"Kolom type '".concat(e.type,"' is niet geregistreerd")}},ColumnPicker:{columnsMenu:"Kolommen",hideColumn:"Verberg Kolom",hideColumnShort:"Verberg"},Filter:{applyFilter:"Pas filter toe",filter:"Filter",editFilter:"Wijzig filter",on:"Aan",before:"Voor",after:"Na",equals:"Is gelijk",lessThan:"Minder dan",moreThan:"Meer dan",removeFilter:"Verwijder filter"},FilterBar:{enableFilterBar:"Maak filterbalk zichtbaar",disableFilterBar:"Verberg filterbalk"},Group:{groupAscending:"Groepeer oplopend",groupDescending:"Groepeer aflopend",groupAscendingShort:"Oplopend",groupDescendingShort:"Aflopend",stopGrouping:"Maak groepering ongedaan",stopGroupingShort:"Maak ongedaan"},Search:{searchForValue:"Zoek op term"},Sort:{sortAscending:"Sorteer oplopend",sortDescending:"Sorteer aflopend",multiSort:"Meerdere sorteringen",removeSorter:"Verwijder sortering",addSortAscending:"Voeg oplopende sortering toe",addSortDescending:"Voeg aflopende sortering toe",toggleSortAscending:"Sorteer oplopend",toggleSortDescending:"Sorteer aflopend",sortAscendingShort:"Oplopend",sortDescendingShort:"Aflopend",removeSorterShort:"Verwijder",addSortAscendingShort:"+ Oplopend",addSortDescendingShort:"+ Aflopend"},Tree:{noTreeColumn:"Om de boomstructuur (tree) eigenschap te kunnen gebruiken zet, tree: true"},Grid:{featureNotFound:function(e){return"Eigenschap '".concat(e,"' is niet beschikbaar, controleer of u de optie geimporteerd heeft")},invalidFeatureNameFormat:function(e){return"Ongeldige functienaam '".concat(e,"', moet beginnen met een kleine letter")},removeRow:"Verwijder rij",removeRows:"Verwijder rijen",loadFailedMessage:"Laden mislukt.",moveColumnLeft:"Plaats naar het linker kader",moveColumnRight:"Plaats naar het rechter kader"},GridBase:{loadMask:"Laden...",noRows:"Geen rijen om weer te geven"},PdfExport:{"Waiting for response from server...":"Wachten op antwoord van server..."},ExportDialog:{width:"40em",labelWidth:"12em",exportSettings:"Instellingen exporteren",export:"Exporteren",exporterType:"Paginering beheren",cancel:"Annuleren",fileFormat:"Bestandsformaat",rows:"Rijen",alignRows:"Rijen uitlijnen",columns:"Columns",paperFormat:"Papier formaat",orientation:"Oriëntatatie"},ExportRowsCombo:{all:"Alle rijen",visible:"Zichtbare rijen"},ExportOrientationCombo:{portrait:"Staand",landscape:"Liggend"},SinglePageExporter:{singlepage:"Enkele pagina"},MultiPageExporter:{multipage:"Meerdere pagina's",exportingPage:function(e){var n=e.currentPage,t=e.totalPages;return"Exporteren van de pagina ".concat(n,"/").concat(t)}}};for(var o in a)r[o]=a[o];var i=r,l={SchedulerCommon:{},ExcelExporter:{"No resource assigned":"Geen resource toegewezen"},ResourceInfoColumn:{eventCountText:function(e){return e+" evenement"+(1!==e?"en":"")}},Dependencies:{from:"Van",to:"Naar",valid:"Geldig",invalid:"Ongeldig",Checking:"Controleren…"},EventEdit:{Name:"Naam",Resource:"Resource",Start:"Start",End:"Eind",Save:"Bewaar",Delete:"Verwijder",Cancel:"Annuleer","Edit Event":"Wijzig item",Repeat:"Herhaal"},DependencyEdit:{From:"Van",To:"Tot",Type:"Type",Lag:"Achterstand","Edit dependency":"Afhankelijkheid bewerken",Save:"Bewaar",Delete:"Verwijder",Cancel:"Annuleer",StartToStart:"Begin-Tot-Begin",StartToEnd:"Begin-Tot-Einde",EndToStart:"Einde-Tot-Start",EndToEnd:"Einde-Tot-Einde"},EventDrag:{eventOverlapsExisting:"Gebeurtenis overlapt bestaande gebeurtenis voor deze bron",noDropOutsideTimeline:"Evenement kan niet volledig buiten de tijdlijn worden verwijderd"},Scheduler:{"Add event":"Voeg evenement toe","Delete event":"Verwijder item","Unassign event":"Gebeurtenis ongedaan maken"},HeaderContextMenu:{pickZoomLevel:"Zoom in",activeDateRange:"Datum bereik",startText:"Start datum",endText:"Eind datum",todayText:"Vandaag"},EventFilter:{filterEvents:"Filter items",byName:"Op naam"},TimeRanges:{showCurrentTimeLine:"Maak huidige tijdlijn zichtbaar"},PresetManager:{minuteAndHour:{topDateFormat:"ddd DD-MM, hh"},hourAndDay:{topDateFormat:"ddd DD-MM"},weekAndDay:{displayDateFormat:"hh:mm"}},RecurrenceConfirmationPopup:{"delete-title":"U verwijdert een plan item","delete-all-message":"Wilt u alle herhaalde afspraken van dit item verwijderen?","delete-further-message":"Wilt u het geselecteerde en alle toekomstige gebeurtenissen van dit item verwijderen, of aleen het geselecteerde item?","delete-further-btn-text":"Verwijder alleen de toekomstige gebeurtenissen","delete-only-this-btn-text":"Verwijder alleen deze gebeurtenis","update-title":"U verandert een herhaald item","update-all-message":"Wilt u alle herhaalde afspraken van dit item verwijderen?","update-further-message":"Wilt u het geselecteerde en alle toekomstige gebeurtenissen van dit item wijzigen, of aleen het geselecteerde item?","update-further-btn-text":"Wijzig alle toekomstige items","update-only-this-btn-text":"Wijzig alleen dit item",Yes:"Ja",Cancel:"Annuleer",width:600},RecurrenceLegend:{" and ":" en ",Daily:"Dagelijks","Weekly on {1}":function(e){var n=e.days;return"Wekelijks op ".concat(n)},"Monthly on {1}":function(e){var n=e.days;return"Maandelijks op ".concat(n)},"Yearly on {1} of {2}":function(e){var n=e.days,t=e.months;return"Jaarlijks op ".concat(n," ").concat(t)},"Every {0} days":function(e){var n=e.interval;return"Elke ".concat(n," dagen")},"Every {0} weeks on {1}":function(e){var n=e.interval,t=e.days;return"Elke ".concat(n," weken op ").concat(t)},"Every {0} months on {1}":function(e){var n=e.interval,t=e.days;return"Elke ".concat(n," maanden in ").concat(t)},"Every {0} years on {1} of {2}":function(e){var n=e.interval,t=e.days,a=e.months;return"Elke ".concat(n," jaar op ").concat(t," ").concat(a)},position1:"de eerste",position2:"de tweede",position3:"de derde",position4:"de vierde",position5:"de vijfde","position-1":"laatste",day:"dag",weekday:"weekdag","weekend day":"weekend dag",daysFormat:function(e){var n=e.position,t=e.days;return"".concat(n," ").concat(t)}},RecurrenceEditor:{"Repeat event":"Herhaal gebeurtenis",Cancel:"Annuleer",Save:"Bewaar",Frequency:"Frequentie",Every:"Elke",DAILYintervalUnit:"dag(en)",WEEKLYintervalUnit:"week(en) op:",MONTHLYintervalUnit:"maand(en)",YEARLYintervalUnit:"jaren(en) in:",Each:"Elke","On the":"Op de","End repeat":"Einde herhaling","time(s)":"tijd(en)"},RecurrenceDaysCombo:{day:"dag",weekday:"weekdag","weekend day":"weekend dag"},RecurrencePositionsCombo:{position1:"eerste",position2:"tweede",position3:"derde",position4:"vierde",position5:"vijfde","position-1":"laatste"},RecurrenceStopConditionCombo:{Never:"Nooit",After:"Na","On date":"Op datum"},RecurrenceFrequencyCombo:{Daily:"Dagelijks",Weekly:"Wekelijks",Monthly:"Maandelijks",Yearly:"Jaarlijks"},RecurrenceCombo:{None:"Geen","Custom...":"Aangepast..."},ScheduleRangeCombo:{completeview:"Compleet schema",currentview:"Huidige weergave",daterange:"Periode",completedata:"Alle data (events)"},SchedulerExportDialog:{"Schedule range":"Scheduler bereik","Export from":"Vanaf","Export to":"Naar"}};for(var d in i)l[d]=i[d];n.default=l},,,function(e,n,t){"use strict";t.r(n);t(0);n.default={extends:"Nl",Column:{Name:"Naam",City:"Stad",Role:"Rol",Staff:"Personeel",Machines:"Machines",Type:"Type","Task color":"Taakkleur","Employment type":"Type werkgeverschap",Capacity:"Capaciteit","Production line":"Productielijn",Company:"Bedrijf",Start:"Begin",End:"Einde","Nbr tasks":"Numerieke taken","Unassigned tasks":"Niet-toegewezen taken",Duration:"Looptijd"},Shared:{"Locale changed":"Taal is veranderd"},EventEdit:{Location:"Plaats"}}}]).default}));