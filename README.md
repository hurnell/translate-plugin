# Angular Translation Navigator for PhpStorm

A PhpStorm plugin that makes navigating AngularJS and Angular translation keys fast and easy.

Instead of manually searching for translation keys, place the cursor on a translation key and jump directly to the corresponding entry in your language JSON files.

## Features

- Navigate from translation keys to language files
- Supports AngularJS:
    - `translate="something.or.other"`
    - `{{ 'something.or.other' | translate }}`
    - `$translate.instant('something.or.other')`
    - `$translate('something.or.other')`
- Supports Angular:
    - `{{ 'something.or.other' | translate }}`
    - `translate.instant('something.or.other')`
    - `translate.get('something.or.other')`
- Works with JSON language files
- Uses standard **Go to Declaration** (`⌘B` / `Ctrl+B`)

## Example

```html
<button translate="something.or.other"></button>
```

Press **⌘B** (macOS) or **Ctrl+B** (Windows/Linux)

↓

Opens

```json
{
  "settings": {
    "save": "Save"
  }
}
```

## Installation

### JetBrains Marketplace

Coming soon.

### Manual

1. Download the latest ZIP from the Releases page.
2. Open **Settings → Plugins → ⠇ → Install Plugin from Disk...**
3. Select the ZIP file.
4. Restart PhpStorm.

### Change JSON files location
1. Once installed there should be a new item under  **Settings → Tools**.
2. **Settings → Tools → Translation Navigation**.
3. There you'll find **Translation directory path:**
4. Here you can change the relative path of your translations directory (default is **app/languages**).
5. Make sure that the chose path is a relative path! Then click APPLY.

## Supported Frameworks

- AngularJS
- Angular
- ngx-translate
- angular-translate

## Requirements

- PhpStorm 2025.2 or later

## Roadmap

- [ ] Navigate to nested translation keys
- [ ] Find usages of translation keys
- [ ] Auto-completion for translation keys
- [ ] Support YAML translation files
- [ ] Configurable language file locations

## Contributing

Issues and pull requests are welcome.

## License

MIT