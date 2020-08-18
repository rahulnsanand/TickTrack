package com.theflopguyproductions.ticktrack.ui.lottie.model.animatable;

import com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe.TextKeyframeAnimation;
import com.theflopguyproductions.ticktrack.ui.lottie.model.DocumentData;
import com.theflopguyproductions.ticktrack.ui.lottie.value.Keyframe;

import java.util.List;

public class AnimatableTextFrame extends BaseAnimatableValue<DocumentData, DocumentData> {

  public AnimatableTextFrame(List<Keyframe<DocumentData>> keyframes) {
    super(keyframes);
  }

  @Override public TextKeyframeAnimation createAnimation() {
    return new TextKeyframeAnimation(keyframes);
  }
}
