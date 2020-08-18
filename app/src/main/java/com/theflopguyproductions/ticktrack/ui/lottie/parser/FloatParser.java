package com.theflopguyproductions.ticktrack.ui.lottie.parser;


import com.theflopguyproductions.ticktrack.ui.lottie.parser.moshi.JsonReader;

import java.io.IOException;

public class FloatParser implements ValueParser<Float> {
  public static final FloatParser INSTANCE = new FloatParser();

  private FloatParser() {}

  @Override public Float parse(JsonReader reader, float scale) throws IOException {
    return JsonUtils.valueFromObject(reader) * scale;
  }
}