/**
 * @module Core/helper/WalkHelper
 */

/**
 * Tree walking helper
 * @internal
 */
export default class WalkHelper {

    /**
     * Pre-walks any hierarchical data structure
     *
     * @param data Walking starting point
     * @param {Function} childrenFn Function to return `data` children entries in an array
     *                   or null if no children exists for the entry
     * @param {Function} fn Function to call on each entry
     */
    static preWalk(data, childrenFn, fn) {

        const walkStack = [data];
        let node, children;

        while (walkStack.length) {

            node = walkStack.pop();

            fn(node);

            children = childrenFn(node);

            if (children) {
                walkStack.push.apply(walkStack, children.slice().reverse());
            }
        }
    }

    /**
     * Pre-walks any hierarchical data structure, passing along a link to the parent node
     *
     * @param data Walking starting point
     * @param {Function} childrenFn Function to return `data` children entries in an array
     *                   or null if no children exists for the entry
     * @param {Function} fn Function to call on each entry, called with `parent` and `node`
     */
    static preWalkWithParent(data, childrenFn, fn) {

        const walkStack = [{ node : data, parent : null }];

        while (walkStack.length) {
            const { parent, node } = walkStack.pop();

            fn(parent, node);

            const children = childrenFn(node);

            if (children) {
                walkStack.push(...children.slice().reverse().map(child => ({ node : child, parent : node })));
            }
        }
    }

    /**
     * Pre-walk unordered.
     *
     * Like {@link #function-preWalk-static preWalk} but doesn't reverses children before walk,
     * thus children will be walked last child first - first child last
     *
     * @param data Walking starting point
     * @param {Function} childrenFn Function to return `data` children entries in an array
     *                   or null if no children exists for the entry
     * @param {Function} fn Function to call on each entry
     */
    static preWalkUnordered(data, childrenFn, fn) {

        let walkStack = [data],
            node, children;

        while (walkStack.length) {

            node = walkStack[walkStack.length - 1];

            fn(node);

            children = childrenFn(node);

            if (children) {
                walkStack.splice(walkStack.length - 1, 1, ...children);
            }
            else {
                walkStack.length = walkStack.length - 1;
            }
        }
    }

    /**
     * Post-walks any hierarchical data structure
     *
     * @param data Walking starting point
     * @param {Function} childrenFn Function to return `data` children entries in an array
     *                   or null if no children exists for the entry
     * @param {Function} fn Function to call on each entry
     */
    static postWalk(data, childrenFn, fn) {

        let visited = new Map(),
            walkStack = [data],
            node, children;

        while (walkStack.length) {

            node = walkStack[walkStack.length - 1];

            if (visited.has(node)) {
                fn(node);
                walkStack.pop();
            }
            else {
                children = childrenFn(node);

                if (children) {
                    walkStack = walkStack.concat(children.slice().reverse());
                }

                visited.set(node, node);
            }
        }
    }

    /**
     * Pre/Post-walks any hierarchical data structure calling inFn each node when it walks in,
     * and outFn when it walks out.
     *
     * @param data Walking starting point
     * @param {Function} childrenFn Function to return `data` children entries in an array
     *                   or null if no children exists for the entry
     * @param {Function} inFn  Function to call on each entry upon enter
     * @param {Function} outFn Function to call on each entry upon exit
     */
    static prePostWalk(data, childrenFn, inFn, outFn) {

        let visited = new Map(),
            walkStack = [data],
            node, children;

        while (walkStack.length) {

            node = walkStack[walkStack.length - 1];

            if (visited.has(node)) {
                outFn(node);
                walkStack.pop();
            }
            else {
                inFn(node);

                children = childrenFn(node);

                if (children) {
                    walkStack = walkStack.concat(children.slice().reverse());
                }

                visited.set(node, node);
            }
        }
    }
}
