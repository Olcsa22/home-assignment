import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TreeService } from '../../services/tree.service';

@Component({
  selector: 'app-content-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './content-panel.component.html',
  styleUrls: ['./content-panel.component.scss']
})
export class ContentPanelComponent {
  private treeService = inject(TreeService);
  
  readonly selectedNode = this.treeService.selectedNode;
}
