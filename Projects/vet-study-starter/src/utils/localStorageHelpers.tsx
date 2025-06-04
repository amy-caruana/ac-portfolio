// src/utils/localStorageHelpers.ts
export const localStorageKey = (page: number) => `vetapp_notes_page_${page}`;
export const annotationKey = (page: number) => `vetapp_annotations_page_${page}`;


  // utils/localStorageHelpers.ts
  export function saveNotes(fileId: string, page: number, notes: string) {
    const key = `notes_${fileId}_page_${page}`;
    localStorage.setItem(key, notes);
  }

  export function loadNotes(fileId: string, page: number): string {
    const key = `notes_${fileId}_page_${page}`;
    return localStorage.getItem(key) || '';
  }


export const saveAnnotations = (page: number, canvas: any) => {
  if (canvas?.toJSON) {
    const json = canvas.toJSON();
    localStorage.setItem(annotationKey(page), JSON.stringify(json));
  }
};

export const loadAnnotations = (page: number, canvas: fabric.Canvas | null) => {
  const saved = localStorage.getItem(annotationKey(page));
  if (saved && canvas) {
    canvas.loadFromJSON(saved, canvas.renderAll.bind(canvas));
  }
};
