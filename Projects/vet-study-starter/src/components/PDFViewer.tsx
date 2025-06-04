// --- src/components/PDFViewer.tsx ---
import React, { useEffect } from 'react';
import { PDFDocumentProxy } from 'pdfjs-dist';

interface Props {
  pdf: PDFDocumentProxy;
  pageNumber: number;
  scale: number;
  canvasRef: React.RefObject<HTMLCanvasElement>;
}

export default function PDFViewer({
  pdf,
  pageNumber,
  scale,
  canvasRef,
}: Props) {
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!pdf || !canvas) return;

    let renderTask: any = null;

    const render = async () => {
      const page = await pdf.getPage(pageNumber);
      const viewport = page.getViewport({ scale });

      const context = canvas.getContext('2d');
      if (!context) return;

      canvas.width = viewport.width;
      canvas.height = viewport.height;

      renderTask = page.render({ canvasContext: context, viewport });
      await renderTask.promise;
    };

    render();

    return () => {
      renderTask?.cancel?.();
    };
  }, [pdf, pageNumber, scale]);

  return (
    <div className="relative flex flex-col items-center">
      <canvas ref={canvasRef} className="rounded-md shadow border bg-white" />
    </div>
  );
}
