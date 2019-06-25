package com.fw.imbp.rt.transfer;

/**
 * @author Charles(Li) Cai
 * @date 5/25/2019
 */
@FunctionalInterface
public interface OnComplete {

    void onComplete(String endMessage);
}
