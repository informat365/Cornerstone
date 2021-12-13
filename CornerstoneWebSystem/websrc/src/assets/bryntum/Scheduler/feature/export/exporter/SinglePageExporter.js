import GridSinglePageExporter from '../../../../Grid/feature/export/exporter/SinglePageExporter.js';
import SchedulerExporterMixin from './SchedulerExporterMixin.js';

export default class SinglePageExporter extends SchedulerExporterMixin(GridSinglePageExporter) {
    // We should not collect dependencies per each page, instead we'd render them once
    collectDependencies() {}
    
    positionRows(rows) {
        let currentTop  = 0,
            resources   = [],
            events      = [],
            translateRe = /translate\((\d+.?\d*)px, (\d+.?\d*)px\)/;

        // In case of variable row height row vertical position is not guaranteed to increase
        // monotonously. Position row manually instead
        rows.forEach(([html, top, height, eventsHtml]) => {
            resources.push(html.replace(translateRe, `translate($1px, ${currentTop}px)`));
            
            const rowTopDelta = currentTop - top;
            
            eventsHtml && Array.from(eventsHtml.entries()).forEach(([key, [html, box]]) => {
                // Fix event vertical position according to the row top
                box.translate(0, rowTopDelta);
                
                // Store event box to render dependencies later
                this.exportMeta.eventsBoxes.set(String(key), box);
                
                events.push(html.replace(translateRe, `translate($1px, ${box.y}px)`));
            });

            currentTop += height;
        });
        
        return { resources, events };
    }
}
