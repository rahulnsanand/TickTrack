package com.theflopguyproductions.ticktrack.ui.lottie.model.content;

import com.theflopguyproductions.ticktrack.ui.lottie.LottieDrawable;
import com.theflopguyproductions.ticktrack.ui.lottie.animation.content.Content;
import com.theflopguyproductions.ticktrack.ui.lottie.animation.content.ContentGroup;
import com.theflopguyproductions.ticktrack.ui.lottie.model.layer.BaseLayer;

import java.util.Arrays;
import java.util.List;

public class ShapeGroup implements ContentModel {
  private final String name;
  private final List<ContentModel> items;
  private final boolean hidden;

  public ShapeGroup(String name, List<ContentModel> items, boolean hidden) {
    this.name = name;
    this.items = items;
    this.hidden = hidden;
  }

  public String getName() {
    return name;
  }

  public List<ContentModel> getItems() {
    return items;
  }

  public boolean isHidden() {
    return hidden;
  }

  @Override public Content toContent(LottieDrawable drawable, BaseLayer layer) {
    return new ContentGroup(drawable, layer, this);
  }

  @Override public String toString() {
    return "ShapeGroup{" + "name='" + name + "\' Shapes: " + Arrays.toString(items.toArray()) + '}';
  }
}
