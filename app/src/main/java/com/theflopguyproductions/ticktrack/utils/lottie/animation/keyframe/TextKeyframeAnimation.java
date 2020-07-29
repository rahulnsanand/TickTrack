package com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe;

import com.theflopguyproductions.ticktrack.ui.lottie.value.Keyframe;
import com.theflopguyproductions.ticktrack.ui.lottie.model.DocumentData;

import java.util.List;

public class TextKeyframeAnimation extends KeyframeAnimation<DocumentData> {
  public TextKeyframeAnimation(List<Keyframe<DocumentData>> keyframes) {
    super(keyframes);
  }

  @Override DocumentData getValue(Keyframe<DocumentData> keyframe, float keyframeProgress) {
    return keyframe.startValue;
  }
}
