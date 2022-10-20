package com.nerdcoredevelopment.inappbillingdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnimalImageSpecs {
    private int imageSrcDrawableResId;
    private int animalSelectionImageResId;
    private float imageScaleX;
    private float imageScaleY;
    private boolean isAnimalUnlocked;
    private boolean isCurrentlySelected;
}
