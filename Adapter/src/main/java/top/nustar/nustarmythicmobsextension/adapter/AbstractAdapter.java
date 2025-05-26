package top.nustar.nustarmythicmobsextension.adapter;

import lombok.Data;
import lombok.NonNull;

@Data
public abstract class AbstractAdapter<T> implements Adapter<T> {

    @NonNull
    private final T actualObject;
}
