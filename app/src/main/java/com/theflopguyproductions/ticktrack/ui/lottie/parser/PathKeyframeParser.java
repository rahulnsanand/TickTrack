package com.theflopguyproductions.ticktrack.ui.lottie.parser;

import android.graphics.PointF;

import com.theflopguyproductions.ticktrack.ui.lottie.LottieComposition;
import com.theflopguyproductions.ticktrack.ui.lottie.animation.keyframe.PathKeyframe;
import com.theflopguyproductions.ticktrack.ui.lottie.parser.moshi.JsonReader;
import com.theflopguyproductions.ticktrack.ui.lottie.utils.Utils;
import com.theflopguyproductions.ticktrack.ui.lottie.value.Keyframe;

import java.io.IOException;

class PathKeyframeParser {

  private PathKeyframeParser() {}

  static PathKeyframe parse(
      JsonReader reader, LottieComposition composition) throws IOException {
    boolean animated = reader.peek() == JsonReader.Token.BEGIN_OBJECT;
    Keyframe<PointF> keyframe = KeyframeParser.parse(
        reader, composition, Utils.dpScale(), PathParser.INSTANCE, animated);

    return new PathKeyframe(composition, keyframe);
  }
}
