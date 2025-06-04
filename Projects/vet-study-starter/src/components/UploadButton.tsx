// src/components/UploadButton.tsx
import React from 'react';

interface Props {
  onUpload: (e: React.ChangeEvent<HTMLInputElement>) => void;
  fileName?: string | null;
}

export default function UploadButton({ onUpload, fileName }: Props) {
  return (
    <div className="flex justify-center items-center gap-4">
      <input
        type="file"
        accept=".pdf,.pptx"
        onChange={onUpload}
        className="file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-600 file:text-white hover:file:bg-blue-700"
      />
      {fileName && (
        <span className="text-gray-600 text-sm italic">{fileName}</span>
      )}
    </div>
  );
}
