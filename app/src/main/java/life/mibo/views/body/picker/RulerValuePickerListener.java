/*
 *  Created by Sumeet Kumar on 4/15/20 2:59 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/15/20 2:24 PM
 *  Mibo Hexa - app
 */

package life.mibo.views.body.picker;

public interface RulerValuePickerListener {

    void onValueChange(String feetValue);

    void onValueChange(int selectedValue);

    void onIntermediateValueChange(String feetValue);

    void onIntermediateValueChange(int selectedValue);
}
