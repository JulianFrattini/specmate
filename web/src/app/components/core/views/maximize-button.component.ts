import { Component } from '@angular/core';
import { ViewControllerService } from '../../../services/view/view-controller.service';

/**
 * This is the Specmate main component
 */
@Component({
    moduleId: module.id,
    selector: 'maximize-button',
    templateUrl: 'maximize-button.component.html',
    styleUrls: ['maximize-button.component.css']
})

export class MaximizeButton {
    constructor(private viewController: ViewControllerService) { }
    
    public maximize(): void {
        this.viewController.maximizeEditor();
    }

    public unmaximize(): void {
        this.viewController.unmaximizeEditor();
    }

    public get isMaximized(): boolean {
        return this.viewController.isEditorMaximized;
    }

}