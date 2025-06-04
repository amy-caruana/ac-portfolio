// --- src/components/AnnotationControls.tsx ---
import React from 'react';

interface Props {
  isAnnotating: boolean;
  setIsAnnotating: (val: boolean) => void;
  brushColor: string;
  setBrushColor: (color: string) => void;
  save: () => void;
  undo: () => void;
  clear: () => void;
}

export default function AnnotationControls({
  isAnnotating,
  setIsAnnotating,
  brushColor,
  setBrushColor,
  save,
  undo,
  clear,
}: Props) {
  return (
    <div className="flex flex-wrap gap-3 justify-center">
      <button
        onClick={() => setIsAnnotating(!isAnnotating)}
        className={`px-4 py-2 rounded text-white ${
          isAnnotating ? 'bg-yellow-600 hover:bg-yellow-700' : 'bg-gray-500 hover:bg-gray-600'
        }`}
      >
        {isAnnotating ? 'Exit' : 'Annotate'}
      </button>
      <button onClick={save} className="px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded">
        💾 Save
      </button>
      <button onClick={undo} className="px-4 py-2 bg-orange-500 hover:bg-orange-600 text-white rounded">
        ↩ Undo
      </button>
      <button onClick={clear} className="px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded">
        ❌ Clear
      </button>
      <input
        type="color"
        value={brushColor}
        onChange={(e) => setBrushColor(e.target.value)}
        className="h-10 w-10 border rounded cursor-pointer"
      />
    </div>
  );
}


