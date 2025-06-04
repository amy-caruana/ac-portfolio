// --- src/components/SlideViewer.tsx ---
import { useEffect, useRef, useState } from 'react';
import { getDocument, GlobalWorkerOptions, PDFDocumentProxy } from 'pdfjs-dist';
import { fabric } from 'fabric';
import { motion } from 'framer-motion';

import UploadButton from './components/UploadButton';
import PDFViewer from './components/PDFViewer';
// import AnnotationControls from './components/AnnotationControls';
import NotesPanel from './components/NotesPanel';
import FlashcardsPanel from './components/FlashcardsPanel';
import {
  saveAnnotations,
  saveNotes,
  loadNotes,
} from './utils/localStorageHelpers';

GlobalWorkerOptions.workerSrc = '/pdf.worker.min.mjs';

export default function SlideViewer() {
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [fileName, setFileName] = useState<string | null>(null);
  const [pdf, setPdf] = useState<PDFDocumentProxy | null>(null);
  const [pageNumber, setPageNumber] = useState(1);
  const [notesMap, setNotesMap] = useState<Record<number, string>>({});
  // const [isAnnotating, setIsAnnotating] = useState(false);
  // const [brushColor, setBrushColor] = useState('#54DDC6FF');
  const [scale, setScale] = useState(0.6);
  const [flashcards, setFlashcards] = useState<string[][]>([]);
  const [currentCard, setCurrentCard] = useState(0);
  const [showAnswer, setShowAnswer] = useState(false);
  const [loadingFlashcards, setLoadingFlashcards] = useState(false);
  const [pdfPath, setPdfPath] = useState<string | null>(null);
  const [questionCount, setQuestionCount] = useState(3);

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  // const fabricRef = useRef<fabric.Canvas | null>(null);
  // const annotationRef = useRef<HTMLCanvasElement | null>(null);

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setFileName(file.name);

    const formData = new FormData();
    formData.append('file', file);

    try {
      const res = await fetch('http://localhost:4000/upload', {
        method: 'POST',
        body: formData,
      });

      if (!res.ok) throw new Error('Upload failed.');

      const data = await res.json();
      setPdfUrl(data.pdfUrl);
      setPdfPath(data.pdfPath);
      setPageNumber(1);
      setFlashcards([]);
    } catch (err) {
      alert('File upload failed.');
      console.error(err);
    }
  };

  useEffect(() => {
    if (!pdfUrl) return;
    getDocument(pdfUrl).promise.then(setPdf).catch(console.error);
  }, [pdfUrl]);


  // load notes when page changes or file changes
  useEffect(() => {
    if (!fileName || !pdf) return;

    const newMap: Record<number, string> = {};
    for (let i = 1; i <= pdf.numPages; i++) {
      newMap[i] = loadNotes(fileName, i);
    }
    setNotesMap(newMap);
  }, [fileName, pdf]);


  // save notes per page + file
  const handleNotesChange = (value: string) => {
    if (!fileName) return;
    setNotesMap(prev => {
      const updated = { ...prev, [pageNumber]: value };
      saveNotes(fileName, pageNumber, value);
      return updated;
    });
  };


  // const undo = () => {
  //   const canvas = fabricRef.current;
  //   if (canvas) {
  //     const objects = canvas.getObjects();
  //     if (objects.length > 0) {
  //       canvas.remove(objects[objects.length - 1]);
  //       canvas.renderAll();
  //     }
  //   }
  // };

  // const clear = () => {
  //   fabricRef.current?.clear();
  // };

  const generateFlashcards = async () => {
    if (!pdfPath) {
      alert('Upload a PDF first.');
      return;
    }

    setLoadingFlashcards(true);
    try {
      const res = await fetch('http://localhost:4000/generate-flashcards', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pdfPath, pageNumber, questionCount }),
      });

      if (!res.ok) throw new Error('Flashcard generation failed.');

      const data = await res.json();
      setFlashcards(data.flashcards);
      setCurrentCard(0);
      setShowAnswer(false);
    } catch (err) {
      console.error(err);
      alert('Failed to generate flashcards.');
    } finally {
      setLoadingFlashcards(false);
    }
  };

  return (
    <div className="min-h-[100dvh] bg-[#ecf4ec] bg-[url('/paw-texture.svg')] bg-repeat bg-fixed bg-[length:520px_390px] py-12 px-4 md:px-8">
      {/* Mascot in corner */}
      <img
        src="/mascot-cat.png"
        className="hidden sm:block w-28 absolute top-6 right-6 drop-shadow-xl z-10"
      />
      <div className="max-w-7xl mx-auto space-y-14">
        <h1 className="text-5xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-indigo-700 to-teal-500 text-center tracking-tight drop-shadow-md leading-tight pt-4 pb-4">
          🐾 Stu: Your AI Study Buddy
        </h1>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4 }}
          className="bg-white/90 backdrop-blur-md p-6 rounded-3xl shadow-2xl border border-gray-200/30 transition-all"
        >

          <UploadButton onUpload={handleUpload} fileName={fileName} />
        </motion.div>

        {pdf && (
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.4 }}
            className="grid lg:grid-cols-2 gap-10"
          >
            <div className="bg-white/70 backdrop-blur-lg rounded-2xl shadow-xl p-6 relative">
              <PDFViewer
                pdf={pdf}
                pageNumber={pageNumber}
                scale={scale}
                canvasRef={canvasRef}
              />

              <div className="mt-6 space-y-4">
                <div className="flex justify-between items-center gap-4">
                  <button onClick={() => setPageNumber(p => Math.max(p - 1, 1))} className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow">
                    ◀ Prev
                  </button>
                  <span className="text-lg font-semibold text-gray-700">
                    Page {pageNumber} / {pdf.numPages}
                  </span>
                  <button onClick={() => setPageNumber(p => Math.min(p + 1, pdf.numPages))} className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow">
                    Next ▶
                  </button>
                </div>

                <div className="flex items-center gap-3">
                  <label className="font-medium text-gray-700">Zoom</label>
                  <input
                    type="range"
                    min="0.5"
                    max="3"
                    step="0.1"
                    value={scale}
                    onChange={(e) => setScale(Number(e.target.value))}
                    className="w-40 accent-blue-600"
                  />
                  <span className="text-sm font-medium text-gray-600">{scale.toFixed(1)}x</span>
                </div>

                {/* <AnnotationControls
                  isAnnotating={isAnnotating}
                  setIsAnnotating={setIsAnnotating}
                  brushColor={brushColor}
                  setBrushColor={setBrushColor}
                  save={() => saveAnnotations(pageNumber, fabricRef.current)}
                  undo={undo}
                  clear={clear}
                /> */}

                <div className="flex flex-col sm:flex-row items-center gap-4 pt-4">
                  <label className="font-medium text-gray-700"># Questions</label>
                  <input
                    type="number"
                    min={1}
                    max={10}
                    value={questionCount}
                    onChange={(e) => setQuestionCount(Number(e.target.value))}
                    className="w-24 border border-gray-300 px-3 py-2 rounded text-center shadow-sm"
                  />
                  <button
                    onClick={generateFlashcards}
                    disabled={loadingFlashcards}
                    className="w-full sm:w-auto px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white font-medium rounded-lg shadow disabled:opacity-50"
                  >
                    {loadingFlashcards ? 'Generating...' : '🧠 Generate Flashcards'}
                  </button>
                </div>
              </div>
            </div>

            <NotesPanel
              notes={notesMap[pageNumber] || ''}
              onChange={handleNotesChange}
              pageNumber={pageNumber}
            />

          </motion.div>
        )}

        <FlashcardsPanel
          flashcards={flashcards}
          currentCard={currentCard}
          setCurrentCard={setCurrentCard}
          showAnswer={showAnswer}
          setShowAnswer={setShowAnswer}
          loading={loadingFlashcards}
        />
      </div>
    </div>
  );
}
