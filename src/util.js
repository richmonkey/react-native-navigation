import {
    processColor    
} from 'react-native';

var resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');

export function processProperties(properties) {
    for (var property in properties) {
        if (properties.hasOwnProperty(property)) {
            if (property === 'icon' || property.endsWith('Icon') || property.endsWith('Image')) {
                if (properties[property]) {
                    properties[property] = resolveAssetSource(properties[property]);
                }
            }
            if (property === 'color' || property.endsWith('Color')) {
                if (properties[property]) {
                    properties[property] = processColor(properties[property]);
                }
            }
        }
    }
}


export function processButtons(buttons) {
    //todo test
    // params.rightButtons.forEach(function(button) {
    //     button.enabled = !button.disabled;
    //     if (button.icon) {
    //       const icon = resolveAssetSource(button.icon);
    //       if (icon) {
    //         button.icon = icon.uri;
    //       }
    //     }
    if (!buttons) return;
    for (var i = 0 ; i < buttons.length ; i++) {
        buttons[i] = Object.assign({}, buttons[i]);
        var button = buttons[i];
        processProperties(button);
        if (button.title && typeof(button.title) == "function") {
            button.title = button.title();
        }
    }
}
