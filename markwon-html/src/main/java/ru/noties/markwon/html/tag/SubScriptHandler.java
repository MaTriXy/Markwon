package ru.noties.markwon.html.tag;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.MarkwonConfiguration;
import ru.noties.markwon.RenderProps;
import ru.noties.markwon.html.HtmlTag;
import ru.noties.markwon.html.span.SubScriptSpan;

public class SubScriptHandler extends SimpleTagHandler {
    @Nullable
    @Override
    public Object getSpans(@NonNull MarkwonConfiguration configuration, @NonNull RenderProps renderProps, @NonNull HtmlTag tag) {
        return new SubScriptSpan();
    }
}
