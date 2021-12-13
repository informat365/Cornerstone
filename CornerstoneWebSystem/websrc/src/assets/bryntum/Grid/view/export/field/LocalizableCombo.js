import LocalizableComboItems from '../../../../Core/widget/mixin/LocalizableComboItems.js';
import Combo from '../../../../Core/widget/Combo.js';
import BryntumWidgetAdapterRegister from '../../../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';

export default class LocalizableCombo extends LocalizableComboItems(Combo) { }

BryntumWidgetAdapterRegister.register('localizablecombo', LocalizableCombo);
