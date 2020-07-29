package com.theflopguyproductions.ticktrack.ui.lottie.model.animatable;

import android.graphics.Path;

import com.theflopguyproductions.ticktrack.ui.lottie.value.Keyframe;
import com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe.BaseKeyframeAnimation;
import com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe.ShapeKeyframeAnimation;
import com.theflopguyproductions.ticktrack.ui.lottie.model.content.ShapeData;

import java.util.List;

public class AnimatableShapeValue extends BaseAnimatableValue<ShapeData, Path> {

  public AnimatableShapeValue(List<Keyframe<ShapeData>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<ShapeData, Path> createAnimation() {
    return new ShapeKeyframeAnimation(keyframes);
  }
}
