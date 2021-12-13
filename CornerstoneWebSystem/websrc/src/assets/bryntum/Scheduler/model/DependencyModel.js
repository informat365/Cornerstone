import DependencyBaseModel from './DependencyBaseModel.js';

/**
 * @module Scheduler/model/DependencyModel
 */

/**
 * This class represents a single Dependency between two events. It is a subclass of the {@link Scheduler.model.DependencyBaseModel}
 * class, which in its turn subclasses {@link Core.data.Model}.
 * Please refer to documentation of those classes to become familiar with the base interface of this class.
 *
 * A Dependency has the following fields:
 *
 * - `id` - The id of the dependency itself
 * - `from` - The id of the event at which the dependency starts
 * - `to` - The id of the event at which the dependency ends
 * - `cls` - A CSS class that will be applied to each rendered dependency DOM element
 * - `type` - An integer constant representing the type of the dependency:
 *   - 0 - start-to-start dependency
 *   - 1 - start-to-end dependency
 *   - 2 - end-to-start dependency
 *   - 3 - end-to-end dependency
 * - `bidirectional` - A boolean indicating if a dependency goes both directions (default false)
 *
 * Subclassing the Dependency class
 * --------------------
 *
 * The name of any fields data source can be customized in the subclass, see the example below. Please also refer to {@link Core.data.Model}
 * for details.
 * @example
 * class MyDependency extends DependencyModel {
 *       static get fields() {
 *           return [
 *               { name: 'to', dataSource: 'targetId' },
 *               { name: 'from', dataSource: 'sourceId' }
 *           ]);
 *       }
 *
 *       ...
 *  }
 *
 * @extends Scheduler/model/DependencyBaseModel
 */
export default class DependencyModel extends DependencyBaseModel {
    // Determines the type of dependency based on fromSide and toSide
    // TODO: Check with vertical orientation
    getTypeFromSides(fromSide, toSide, rtl) {
        const types     = DependencyBaseModel.Type,
            startSide = rtl ? 'right' : 'left',
            endSide   = rtl ? 'left' : 'right';

        if (fromSide === startSide) {
            return (toSide === startSide) ? types.StartToStart : types.StartToEnd;
        }

        return (toSide === endSide) ? types.EndToEnd : types.EndToStart;
    }
}

DependencyModel.exposeProperties();
