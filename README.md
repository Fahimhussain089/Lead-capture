# Lead capture

Android lead form built with Kotlin and Jetpack Compose. Fields are rendered from a config list. There is no form library and no XML layout.

Min SDK 26. Compile and target SDK 35.

## Build and run

Open this folder in Android Studio, let Gradle sync, then run the `app` configuration.

From a terminal, point `JAVA_HOME` at Android Studio's JDK and use the wrapper:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

Emulators:

- Compact (one column): a phone portrait AVD, for example Pixel 6. Portrait width is under 600dp.
- Expanded (two columns): rotate that same emulator to landscape, or use any AVD whose width is at least 600dp. A tablet system image is installed under `android-35 / google_apis_playstore_tablet` if you want a tablet AVD.

The form keeps its values across rotation.

## Package layout

```
com.assignment.dynamicform
├── MainActivity.kt
├── designsystem
│   ├── token          color, spacing, type, layout breakpoint
│   ├── theme          Material theme wired to those tokens
│   ├── atom           text field, select, checkbox, button
│   ├── molecule       label, control, hint, error
│   └── form           config model, column layout, dynamic form
└── lead               config, validation, screen
```

## Where things live

| Piece | File |
| --- | --- |
| Color, spacing, type | `designsystem/token/ColorTokens.kt`, `SpacingTokens.kt`, `TypeTokens.kt` |
| Two-column breakpoint (600dp) and max form width | `designsystem/token/LayoutTokens.kt` |
| Atoms | `designsystem/atom/DsTextField.kt`, `DsSelect.kt`, `DsCheckbox.kt`, `DsButton.kt` |
| Field molecule | `designsystem/molecule/FieldMolecule.kt` |
| Form that maps a control type to an atom | `designsystem/form/DynamicForm.kt` |
| Lead config | `lead/LeadFormConfig.kt` |
| Validation (pure function of config + values) | `lead/LeadFormValidator.kt` |
| Screen, submit, and the value summary | `lead/LeadFormScreen.kt` |

Atoms take a value, a label, and callbacks. They do not know what a lead is. Adding a field means adding an entry to `LeadFormConfig.fields`.

## Compact and expanded layout

`designsystem/form/FormColumns.kt` owns both layouts. It reads `LayoutTokens.expandedMinWidth`.

- Below 600dp: one column, every field full width.
- At 600dp and above: half-span fields share a row. Full name and email are half, so they share the first row. Notes and consent are full width, so each spans the row.

`lead/LeadFormScreen.kt` owns insets and the keyboard. Content is padded by system bars, display cutout, and the IME as a union, so the focused field can scroll above the keyboard and the submit button stays on screen above the navigation bar and the keyboard. `adjustResize` is set on the activity.
