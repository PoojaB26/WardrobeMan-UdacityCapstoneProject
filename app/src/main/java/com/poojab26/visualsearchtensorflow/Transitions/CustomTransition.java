package com.poojab26.visualsearchtensorflow.Transitions;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;

/**
 * Created by poojab26 on 28-May-18.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CustomTransition extends TransitionSet {
    public CustomTransition() {
        init();
    }

    /**
     * This constructor allows us to use this transition in XML
     */
    public CustomTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrdering(ORDERING_SEQUENTIAL);
        addTransition(new ChangeImageTransform()).

                addTransition(new ChangeTransform()).
                        addTransition(new ChangeBounds()).addTransition(new ChangeTransform());

    }
}
