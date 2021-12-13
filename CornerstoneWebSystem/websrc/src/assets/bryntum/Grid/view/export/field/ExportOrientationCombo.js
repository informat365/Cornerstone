import BryntumWidgetAdapterRegister from '../../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import { Orientation } from '../../../feature/export/Utils.js';
import LocalizableCombo from './LocalizableCombo.js';

export default class ExportOrientationCombo extends LocalizableCombo {
    static get $name() {
        return 'ExportOrientationCombo';
    }
    
    static get defaultConfig() {
        return {
            editable : false
        };
    }
    
    buildLocalizedItems() {
        const me = this;
        
        return [
            { id : Orientation.portrait, text : me.L(Orientation.portrait) },
            { id : Orientation.landscape, text : me.L(Orientation.landscape) }
        ];
    }
}

BryntumWidgetAdapterRegister.register(ExportOrientationCombo.$name, ExportOrientationCombo);
