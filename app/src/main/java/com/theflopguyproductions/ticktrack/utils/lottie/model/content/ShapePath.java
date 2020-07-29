package com.theflopguyproductions.ticktrack.ui.lottie.model.content;

import com.theflopguyproductions.ticktrack.ui.lottie.LottieDrawable;
import com.theflopguyproductions.ticktrack.ui.lottie.animation.content.Content;
import com.theflopguyproductions.ticktrack.ui.lottie.animation.content.ShapeContent;
import com.theflopguyproductions.ticktrack.ui.lottie.model.animatable.AnimatableShapeValue;
import com.theflopguyproductions.ticktrack.ui.lottie.model.layer.BaseLayer;

public class ShapePath implements ContentModel {
  private final String name;
  private final int index;
  private final AnimatableShapeValue shapePath;
  private final boolean hidden;

  public ShapePath(String name, int index, AnimatableShapeValue shapePath, boolean hidden) {
    this.name = name;
    this.index = index;
    this.shapePath = shapePath;
    this.hidden = hidden;
  }

  public String getName() {
    return name;
  }

  public AnimatableShapeValue getShapePath() {
    return shapePath;
  }

  @Override public Content toContent(LottieDrawable drawable, BaseLayer layer) {
    return new ShapeContent(drawable, layer, this);
  }

  public boolean isHidden() {
    return hidden;
  }

  @Override public String toString() {
    return "ShapePath{" + "name=" + name +
        ", index=" + index +
        '}';
  }
}
