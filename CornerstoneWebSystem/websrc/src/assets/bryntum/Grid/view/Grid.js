//region Import

import GridBase from './GridBase.js';
import BryntumWidgetAdapterRegister from '../../Core/adapter/widget/util/BryntumWidgetAdapterRegister.js';
import VersionHelper from '../../Core/helper/VersionHelper.js';

// import default features (might be able to skip this when draft on dynamic import is implemented)
import '../feature/CellEdit.js';
import '../feature/ColumnDragToolbar.js';
import '../feature/ColumnPicker.js';
import '../feature/ColumnReorder.js';
import '../feature/ColumnResize.js';
import '../feature/ContextMenu.js';
import '../feature/Filter.js';
import '../feature/FilterBar.js';
import '../feature/Group.js';
import '../feature/Sort.js';
import '../feature/Stripe.js';

// To enable using checkbox selection mode, moved here to not be dragged into GridBase automatically
import '../column/CheckColumn.js';

//endregion

/**
 * @module Grid/view/Grid
 */

/**
 * The Grid component is a very powerful and performant UI component that shows tabular data (or tree data using the {@link Grid.view.TreeGrid}).
 *
 * <h2>Intro</h2>
 * The Grid widget has a wide range of features and a large API to allow users to work with data efficiently in the browser. The two
 * most important configs are {@link #config-store} and {@link #config-columns}. With the store config, you decide which data to load into the grid.
 * You can work with both in-memory arrays or load data using ajax. See the {@link Core.data.Store} class to learn more about loading data into stores.
 *
 * The columns config accepts an array of {@link Grid.column.Column Column} descriptors defining which fields that will be displayed in the grid.
 * The {@link Grid.column.Column#config-field} property in the column descriptor maps to a field in your dataset. The simplest grid configured with inline data and two columns would
 * look like this:
 *
 *      let grid = new Grid({
 *          appendTo : document.body,
 *
 *          columns: [
 *              { field: 'name', text: 'Name' },
 *              { field: 'job', text: 'Job', renderer: ({value}) => value ? value : 'Unemployed' }
 *          ],
 *
 *          data: [
 *              { name: 'Bill', job: 'Retired' },
 *              { name: 'Elon', job: 'Visionary' },
 *              { name: 'Me' }
 *          ]
 *      });
 *
 * {@inlineexample grid/Grid.js}
 * <h2>Features</h2>
 * To avoid the Grid core being bloated, its main features are implemented in separate ´feature´ classes. These can be turned on and off based
 * on your requirements. To configure (or disable) a feature, use the {@link #config-features} object to provide your desired configuration for the features
 * you want to use. Each feature has an ´id´ that you use as a key in the features object:
 *
 *      let grid = new Grid({
 *          appendTo : document.body,
 *
 *          features : {
 *              cellEdit     : false,
 *              regionResize : true,
 *              cellTooltip  : {
 *                  tooltipRenderer : (data) => {
 *                  }
 *              },
 *              ...
 *          }
 *      });
 *
 * {@region Column configuration options}
 * A grid contains a number of columns that control how your data is rendered. The simplest option is to simply point a Column to a field in your dataset, or define a custom {@link Grid.column.Column#config-renderer}.
 * The renderer function receives one object parameter containing rendering data for the current cell being rendered.
 *
 *      let grid = new Grid({
 *          appendTo : document.body,
 *
 *          columns: [
 *              {
 *                  field: 'task',
 *                  text: 'Task',
 *                  renderer: (renderData) => {
 *                      const record = renderData.record;
 *
 *                      if (record.percentDone === 100) {
 *                          renderData.cellElement.classList.add('taskDone');
 *                          renderData.cellElement.style.background = 'green';
 *                      }
 *
 *                      return renderData.value;
 *                  }
 *              }
 *          ]
 *      });
 *
 * {@endregion}
 * {@region Grid sections (aka "locked" or "frozen" columns)}
 * The grid can be divided horizontally into individually scrollable sections. This is great if you have lots of columns that
 * don't fit the available width of the screen. To enable this feature, simply mark the columns you want to `lock`.
 * Locked columns are then displayed in their own section to the left of the other columns:
 *
 *      let grid = new Grid({
 *          appendTo : document.body,
 *          width    : 500,
 *          subGridConfigs : {
 *              // set a fixed locked section width if desired
 *              locked : { width: 300 }
 *          },
 *          columns : [
 *              { field : 'name', text : 'Name', width : 200, locked : true },
 *              { field : 'firstName', text : 'First name', width : 100, locked : true },
 *              { field : 'surName', text : 'Last name', width : 100, locked : true },
 *              { field : 'city', text : 'City', width : 100 },
 *              { type : 'number', field : 'age', text : 'Age', width : 200 },
 *              { field : 'food', text : 'Food', width : 200 }
 *          ]
 *      });
 *
 * {@inlineexample grid/LockedGrid.js}
 * You can also move columns between sections by using drag and drop, or use the built-in header context menu. If you want to be able to resize the
 * locked grid section, enable the {@link Grid.feature.RegionResize regionResize} feature.
 * {@endregion}
 * {@region Filtering}
 * One important requirement of a good Grid component is the ability to filter large datasets to quickly find what you're looking for. To
 * enable filtering (through the context menu), add the {@link Grid.feature.Filter filter} feature:
 *
 *      let grid = new Grid({
 *          features: {
 *              filter: true
 *          }
 *      });
 *
 * Or activate a default filter at initial rendering:
 *
 *      let grid = new Grid({
 *          features: {
 *              filter: { property : 'city', value : 'New York' }
 *          }
 *      });
 *
 * {@inlineexample feature/Filter.js}
 * {@endregion}
 * {@region Tooltips}
 * If you have a data models with many fields, and you want to show
 * additional data when hovering over a cell, use the {@link Grid.feature.CellTooltip cellTooltip} feature. To show a tooltip for all cells:
 *
 *      let grid = new Grid({
 *          features: {
 *              cellTooltip: ({value}) => value
 *          }
 *      });
 *
 * {@inlineexample feature/CellTooltip.js}
 * {@endregion}
 * {@region Inline Editing (default <strong>on</strong>)}
 * To enable inline cell editing in the grid, simply add the {@link Grid.feature.CellEdit cellEdit} feature:
 *
 *      let grid = new Grid({
 *          appendTo : document.body,
 *
 *          features : {
 *              cellEdit : true
 *          },
 *          columns: [
 *              {
 *                  field: 'task',
 *                  text: 'Task'
 *              }
 *          ]
 *      });
 *
 * {@inlineexample feature/CellEdit.js}
 * {@endregion}
 * {@region Context Menu}
 * Use the {@link Grid.feature.ContextMenu contextMenu} feature if you want your users to be able to interact with the data through the context menu:
 *
 *      let grid = new Grid({
 *          features: {
 *              contextMenu: {
 *                  headerItems: [
 *                      {
 *                          text: 'Show info',
 *                          icon: 'fa fa-info-circle',
 *                          weight: 200,
 *                          onItem : ({ item }) => console.log(item.text)
 *                      }
 *                  ],
 *
 *              cellItems: [
 *                  { text: 'Show options', icon: 'fa fa-gear', weight: 200 }
 *              ]
 *          }
 *      }
 *
 * {@inlineexample feature/ContextMenu.js}
 * {@endregion}
 * {@region Grouping}
 * To group rows by a field in your dataset, use the {@link Grid.feature.Group group} feature.
 * {@inlineexample feature/Group.js}
 * {@endregion}
 * {@region Searching}
 * When working with lots of data, a quick alternative to filtering is the {@link Grid.feature.Search search} feature. It highlights
 * matching values in the grid as you type.
 * {@inlineexample feature/Search.js}
 * {@endregion}
 * {@region Loading and saving data}
 * The grid keeps all its data in a {@link Core.data.Store}, which is essentially an Array of {@link Core.data.Model Model} items.
 * You define your own Model representing your data entities and use the Model API to get and set values.
 *
 *      class Person extends Model {}
 *
 *      let person = new Person({
 *          name: 'Steve',
 *          age: 38
 *      });
 *
 *      person.name = 'Linda'; // person object is now `dirty`
 *
 *      let store = new Store({
 *          data : [
 *              { name : 'Don', age : 40 }
 *          ]
 *      });
 *
 *      store.add(person);
 *
 *      console.log(store.count()); // === 2
 *
 *      store.remove(person); // Remove from store
 *
 * When you update a record in a store, it's considered dirty, until you call {@link Core.data.mixin.StoreCRUD#function-commit commit} on the containing Store. You can also configure your Store to commit automatically (like Google docs).
 * If you use an AjaxStore, it will send changes to your server when commit is called.
 * Any changes you make to the Store or its records are immediately reflected in the Grid, so there is no need to tell it to refresh manually.
 *
 * To learn more about loading and saving data, please refer to [this guide](#guides/data/displayingdata.md).
 * {@endregion}
 * {@region Default configs}
 * There is a myriad of configs and features available for Grid, some of them on by default and some of them requiring
 * extra configuration. The code below tries to illustrate the major things that are used by default:
 *
 * ```javascript
 * let grid = new Grid({
 *    // The following features are enabled by default:
 *    features : {
 *        cellEdit      : true,
 *        columnPicker  : true,
 *        columnReorder : true,
 *        columnResize  : true,
 *        contextMenu   : true,
 *        group         : true,
 *        sort          : true
 *    },
 *
 *    animateRemovingRows       : true,  // Rows will slide out on removal
 *    autoHeight                : false, // Grid needs to have a height supplied through CSS (strongly recommended) or by specifying `height`
 *    columnLines               : true,  // Themes might override it to hide lines anyway
 *    emptyText                 : 'No rows to display',
 *    enableTextSelection       : false, // Not allowed to select text in cells by default,
 *    fillLastColumn            : true,  // By default the last column is stretched to fill the grid
 *    fullRowRefresh            : true,  // Refreshes entire row when a cell value changes
 *    loadMask                  : 'Loading...',
 *    resizeToFitIncludesHeader : true,  // Also measure header when auto resizing columns
 *    responsiveLevels : {
 *      small : 400,
 *      medium : 600,
 *      large : '*'
 *    },
 *    rowHeight                  : null,  // Determined using CSS, it will measure rowHeight
 *    showDirty                  : false, // No indicator for changed cells
 *    showRemoveRowInContextMenu : true   // Context menu has "Remove row" item
 * });
 * ```
 * {@endregion}
 * {@region Performance}
 * In general the Grid widget has very good performance and you can try loading any amount of data in the <a target="_blank" href="../examples/bigdataset">bigdataset</a> demo.
 * The overall rendering performance is naturally affected by many other things than
 * the data volume. Other important factors that can impact performance: number of columns, complex cell renderers, locked columns, the number of features enabled
 * and of course the browser (Chrome fastest, IE slowest).
 * {@endregion}
 *
 * @extends Grid/view/GridBase
 * @classType grid
 */
export default class Grid extends GridBase {
    static get $name() {
        return 'Grid';
    }
}

BryntumWidgetAdapterRegister.register('grid', Grid);

VersionHelper.setVersion('grid', '3.0.0');
