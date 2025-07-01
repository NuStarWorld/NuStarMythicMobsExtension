package top.nustar.nustarmythicmobsextension.service;


import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : NuStar
 * Date : 2025/7/1 20:02
 * Website : <a href="https://www.nustar.top">nustar's web</a>
 * Github : <a href="https://github.com/nustarworld">nustar's github</a>
 * QQ : 3318029085
 */
public interface TypeService<T> {
    T getType();

    static  <R extends TypeService<K>, K> Map<K, R> buildServiceMap(List<R> services) {
        return services.stream().collect(Collectors.toMap(TypeService::getType, Function.identity()));
    }
}
