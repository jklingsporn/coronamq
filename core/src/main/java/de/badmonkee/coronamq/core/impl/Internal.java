package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.CoronaMqOptions;

/**
 * @author jensklingsporn
 */
class Internal {

    private Internal(){}

    public static final int CODE_ERROR_DISPATCH = 1;
    public static final int CODE_ERROR_UPDATE = 2;
    public static final int CODE_ERROR_REQUEST = 3;
    public static final int CODE_ERROR_FAIL = 4;

    static String toWorkerAddress(CoronaMqOptions options, String label){
        return options.getWorkerAddress()+label;
    }


}
