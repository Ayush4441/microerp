/*
 * The MIT License (MIT)
 *
 * Copyright © 2025 Ayush Samantaray (@Ayush4441)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package microerp.util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileChooserUtil {

    public static File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Upload");
        
        // Optional: Restrict to certain file types
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Word Documents", "*.docx"),
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        // Show file chooser — assume primaryStage is globally accessible or fallback to dummy stage
        Stage dummyStage = new Stage();  // fallback stage
        File selectedFile = fileChooser.showOpenDialog(dummyStage);
        return selectedFile;
    }
}
