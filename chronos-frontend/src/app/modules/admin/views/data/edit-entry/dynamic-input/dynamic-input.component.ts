import { Component, inject, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { DateInputComponent } from 'src/app/common/components/date-input/date-input.component';
import { WikiArticleInputComponent } from 'src/app/common/components/wiki-article-input/wiki-article-input.component';
import { AttributeAO } from 'src/app/common/model/schema/admin/attribute.ao';
import { AttributeTypeDTO } from 'src/app/common/model/schema/attribute-type.dto';
import { FormService } from 'src/app/common/util/form.service';

@Component({
  selector: 'chronos-dynamic-input',
  imports: [
    DateInputComponent,
    ReactiveFormsModule,
    WikiArticleInputComponent,
  ],
  templateUrl: './dynamic-input.component.html',
  styleUrl: './dynamic-input.component.scss',
})
export class DynamicInputComponent {
  // Injected Dependencies
  private formService = inject(FormService);

  // Inputs
  public attribute = input.required<AttributeAO>();
  public form = input.required<FormGroup>();

  // Constants for template
  protected readonly STRING = AttributeTypeDTO.STRING;
  protected readonly ENUM = AttributeTypeDTO.ENUM;
  protected readonly NUMBER = AttributeTypeDTO.NUMBER;
  protected readonly DATENOTATION = AttributeTypeDTO.DATENOTATION;
  protected readonly WIKIQID = AttributeTypeDTO.WIKIQID;

  // Methods

  protected isInvalid(): boolean {
    const field = this.attribute().key!;
    const ctrl = this.form()?.get(field);
    return !!(ctrl?.invalid && ctrl?.touched);
  }

  protected errors(): string[] {
    return this.formService.extractErrors(this.attribute().key!, this.attribute().key!, this.form());
  }


}
