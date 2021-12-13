import { QUEUE_PROP, POS_PROP } from './Props.js';

export const resetQueue = (stm, options) => {
    const { undo, redo } = options;

    let newProps;

    if (undo && !redo) {
        newProps = {
            [QUEUE_PROP] : stm[QUEUE_PROP].slice(stm.position),
            [POS_PROP]   : 0
        };
    }
    else if (redo && !undo) {
        newProps = {
            [QUEUE_PROP] : stm[QUEUE_PROP].slice(0, stm.position)
        };
    }
    else {
        newProps = {
            [QUEUE_PROP] : [],
            [POS_PROP]   : 0
        };
    };

    return [
        newProps,
        () => {
            stm.notifyStoresAboutQueueReset(options);
        }
    ];
};
