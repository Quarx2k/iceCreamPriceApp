package com.example.quarx2k.icecreamprice;

import io.realm.RealmObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by quarx2k on 05.05.15.
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class IceCream extends RealmObject {
    private String name;
    private Float price;
    private Float newPrice;
    private Integer stock;
    private Float sum;
    }