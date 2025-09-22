package instafel.app.managers;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import instafel.app.R;

public class AttributeManager {
    public static int ifl_attr_ui_titleText = 0;
    public static int ifl_attr_ui_subtitleText = 1;
    public static int ifl_attr_ui_iconRes = 2;
    public static int ifl_attr_ui_spaceBottom = 3;
    public static int ifl_attr_ui_spaceTop = 4;
    public static int ifl_attr_ui_enableSubIcon = 5;
    public static int ifl_attr_ui_subIconRes = 6;
    public static int ifl_attr_ui_iconResTint = 7;
    public static int ifl_attr_ui_iconPadding = 8;
    public static int ifl_attr_ui_enableIcon = 9;

    private Context ctx;
    private AttributeSet attrs;

    public AttributeManager(Context ctx, AttributeSet attrs) {
        this.ctx = ctx;
        this.attrs = attrs;
    }

    private TypedArray getTypedArray(int attrCode) {
        int[] attrsArray = new int[1];
        attrsArray[0] = 114;
        switch (attrCode) {
            case 0:
                attrsArray[0] = R.attr.ifl_attr_ui_titleText;
                break;
            case 1:
                attrsArray[0] = R.attr.ifl_attr_ui_subtitleText;
                break;
            case 2:
                attrsArray[0] = R.attr.ifl_attr_ui_iconRes;
                break;
            case 3:
                attrsArray[0] = R.attr.ifl_attr_ui_spaceBottom;
                break;
            case 4:
                attrsArray[0] = R.attr.ifl_attr_ui_spaceTop;
                break;
            case 5:
                attrsArray[0] = R.attr.ifl_attr_ui_enableSubIcon;
                break;
            case 6:
                attrsArray[0] = R.attr.ifl_attr_ui_subIconRes;
                break;
            case 7:
                attrsArray[0] = R.attr.ifl_attr_ui_iconResTint;
                break;
            case 8:
                attrsArray[0] = R.attr.ifl_attr_ui_iconPadding;
                break;
            case 9:
                attrsArray[0] = R.attr.ifl_attr_ui_enableIcon;
                break;
        }
        TypedArray a = ctx.obtainStyledAttributes(attrs, attrsArray);
        return a;
    }

    public void recycleTypedArray() {
        getTypedArray(0).recycle();
    }

    public String getString(int attrId) {
        return getTypedArray(attrId).getString(0);
    }

    public String getString(int attrId, String def) {
        return getTypedArray(attrId).getString(0);
    }

    public int getInteger(int attrId, int def) {
        return getTypedArray(attrId).getInteger(0, def);
    }

    public boolean getBoolean(int attrId, boolean def) {
        return getTypedArray(attrId).getBoolean(0, def);
    }

    public int getResourceId(int attrId, int def) {
        return getTypedArray(attrId).getResourceId(0, def);
    }
}
