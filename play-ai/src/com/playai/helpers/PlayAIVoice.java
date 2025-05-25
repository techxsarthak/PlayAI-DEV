package com.playai.helpers;

import com.google.appinventor.components.common.OptionList;
import com.google.appinventor.components.common.Default;

import java.util.HashMap;
import java.util.Map;

public enum PlayAIVoice implements OptionList<String> {
    @Default
    Arista("Arista-PlayAI"),
    Atlas("Atlas-PlayAI"),
    Basil("Basil-PlayAI"),
    Briggs("Briggs-PlayAI"),
    Calum("Calum-PlayAI"),
    Celeste("Celeste-PlayAI"),
    Cheyenne("Cheyenne-PlayAI"),
    Chip("Chip-PlayAI"),
    Cillian("Cillian-PlayAI"),
    Deedee("Deedee-PlayAI"),
    Fritz("Fritz-PlayAI"),
    Gail("Gail-PlayAI"),
    Indigo("Indigo-PlayAI"),
    Mamaw("Mamaw-PlayAI"),
    Mason("Mason-PlayAI"),
    Mikail("Mikail-PlayAI"),
    Mitch("Mitch-PlayAI"),
    Quinn("Quinn-PlayAI"),
    Thunder("Thunder-PlayAI");

    private final String voice;

    PlayAIVoice(String voice) {
        this.voice = voice;
    }

    @Override
    public String toUnderlyingValue() {
        return voice;
    }

    private static final Map<String, PlayAIVoice> lookup = new HashMap<>();

    static {
        for (PlayAIVoice v : PlayAIVoice.values()) {
            lookup.put(v.voice, v);
        }
    }

    public static PlayAIVoice fromUnderlyingValue(String voice) {
        return lookup.getOrDefault(voice, Arista);
    }
}
