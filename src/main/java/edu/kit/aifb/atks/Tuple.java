package edu.kit.aifb.atks;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Tuple<T1, T2> {
    private final T1 left;
    private final T2 right;

    public Tuple(T1 left, T2 right) {
        this.left = left;
        this.right = right;
    }
}
