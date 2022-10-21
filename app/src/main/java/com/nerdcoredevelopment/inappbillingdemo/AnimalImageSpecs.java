package com.nerdcoredevelopment.inappbillingdemo;

import androidx.appcompat.widget.AppCompatImageView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnimalImageSpecs {
    private int imageSrcDrawableResId;
    private AppCompatImageView animalSelectionImageView;
    private float imageScaleX;
    private float imageScaleY;
    private boolean isAnimalUnlocked;
    private boolean isCurrentlySelected;
    private String sharedPreferencesSelectionKey;
}
