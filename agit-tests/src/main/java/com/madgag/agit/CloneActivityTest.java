package com.madgag.agit;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.EditText;

public class CloneActivityTest extends ActivityInstrumentationTestCase2<Clone> {
    private EditText editText;
    
	public CloneActivityTest() {
		super(Clone.class);
	}

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final Clone a = getActivity();
        editText = (EditText) a.findViewById(R.id.CloneUrlEditText);
    }
    
	@SmallTest
    public void testPreconditions() {
		assertEquals("", editText.getText().toString());
    }
}
