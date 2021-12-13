import GridMultiPageExporter from '../../../../Grid/feature/export/exporter/MultiPageExporter.js';
import SchedulerExporterMixin from './SchedulerExporterMixin.js';

export default class MultiPageExporter extends SchedulerExporterMixin(GridMultiPageExporter) {
    async stateNextPage(config) {
        await super.stateNextPage(config);
        
        this.exportMeta.eventsBoxes.clear();
    }
    
    positionRows(rows) {
        const
            resources   = [],
            events      = [];
        
        // In case of variable row height row vertical position is not guaranteed to increase
        // monotonously. Position row manually instead
        rows.forEach(([html, top, height, eventsHtml]) => {
            resources.push(html);
            eventsHtml && Array.from(eventsHtml.entries()).forEach(([key, [html, box]]) => {
                events.push(html);
    
                // Store event box to render dependencies later
                this.exportMeta.eventsBoxes.set(String(key), box);
            });
        });
        
        return { resources, events };
    }
}
