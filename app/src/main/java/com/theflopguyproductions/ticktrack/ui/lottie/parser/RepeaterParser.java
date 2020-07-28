package com.theflopguyproductions.ticktrack.ui.lottie.parser;


import com.theflopguyproductions.ticktrack.ui.lottie.LottieComposition;
import com.theflopguyproductions.ticktrack.ui.lottie.model.animatable.AnimatableFloatValue;
import com.theflopguyproductions.ticktrack.ui.lottie.model.animatable.AnimatableTransform;
import com.theflopguyproductions.ticktrack.ui.lottie.model.content.Repeater;
import com.theflopguyproductions.ticktrack.ui.lottie.parser.moshi.JsonReader;

import java.io.IOException;

class RepeaterParser {

  private static JsonReader.Options NAMES = JsonReader.Options.of(
      "nm",
      "c",
      "o",
      "tr",
      "hd"
  );

  private RepeaterParser() {
  }

  static Repeater parse(
      JsonReader reader, LottieComposition composition) throws IOException {
    String name = null;
    AnimatableFloatValue copies = null;
    AnimatableFloatValue offset = null;
    AnimatableTransform transform = null;
    boolean hidden = false;

    while (reader.hasNext()) {
      switch (reader.selectName(NAMES)) {
        case 0:
          name = reader.nextString();
          break;
        case 1:
          copies = AnimatableValueParser.parseFloat(reader, composition, false);
          break;
        case 2:
          offset = AnimatableValueParser.parseFloat(reader, composition, false);
          break;
        case 3:
          transform = AnimatableTransformParser.parse(reader, composition);
          break;
        case 4:
          hidden = reader.nextBoolean();
          break;
        default:
          reader.skipValue();
      }
    }

    return new Repeater(name, copies, offset, transform, hidden);
  }
}
