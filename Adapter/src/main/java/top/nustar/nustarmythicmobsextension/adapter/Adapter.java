package top.nustar.nustarmythicmobsextension.adapter;

import team.idealstate.sugar.validate.annotation.NotNull;

public interface Adapter<T> {

    @NotNull
    T getActualObject();
}
