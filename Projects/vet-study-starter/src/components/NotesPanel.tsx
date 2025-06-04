import { useEffect, useRef, useState } from 'react';

interface Props {
  notes: string;
  onChange: (value: string) => void;
  pageNumber: number;
}

const fonts = ['Arial', 'Georgia', 'Courier New', 'Times New Roman', 'Verdana'];
const fontSizes = ['1', '2', '3', '4', '5', '6', '7']; // 1–7 for execCommand('fontSize')

export default function NotesPanel({ notes, onChange, pageNumber }: Props) {
  const editorRef = useRef<HTMLDivElement>(null);
  const lastPageRef = useRef<number | null>(null);
  const suppressChange = useRef(false);

  const [selectedFont, setSelectedFont] = useState(fonts[0]);
  const [selectedFontSize, setSelectedFontSize] = useState('3');
  const [textColor, setTextColor] = useState('#000000');
  const [bgColor, setBgColor] = useState('#ffffff');

  useEffect(() => {
    if (editorRef.current && lastPageRef.current !== pageNumber) {
      suppressChange.current = true;
      editorRef.current.innerHTML = notes || '';
      lastPageRef.current = pageNumber;
      setTimeout(() => {
        suppressChange.current = false;
      }, 0);
    }
  }, [notes, pageNumber]);

  const handleInput = () => {
    if (suppressChange.current) return;
    const html = editorRef.current?.innerHTML || '';
    onChange(html);
  };

  const applyFormat = (command: string, value?: string) => {
    document.execCommand(command, false, value);
  };

  const handleFontChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const font = e.target.value;
    setSelectedFont(font);
    applyFormat('fontName', font);
  };

  const handleSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const size = e.target.value;
    setSelectedFontSize(size);
    applyFormat('fontSize', size);
  };

  const handleTextColorChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const color = e.target.value;
    setTextColor(color);
    applyFormat('foreColor', color);
  };

  const handleBgColorChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const color = e.target.value;
    setBgColor(color);
    applyFormat('hiliteColor', color);
  };

  return (
    <div className="bg-white/70 backdrop-blur rounded-2xl shadow-xl p-6">
      <h2 className="text-2xl font-semibold mb-4 text-gray-800">
        📝 Notes (Page {pageNumber})
      </h2>

      <div className="mb-3 flex flex-wrap gap-2 items-center">
        <select value={selectedFont} onChange={handleFontChange} className="border rounded px-2 py-1">
          {fonts.map(font => (
            <option key={font} value={font}>{font}</option>
          ))}
        </select>

        <select value={selectedFontSize} onChange={handleSizeChange} className="border rounded px-2 py-1">
          {fontSizes.map(size => (
            <option key={size} value={size}>Size {size}</option>
          ))}
        </select>

        <input type="color" value={textColor} onChange={handleTextColorChange} title="Text color" />
        <input type="color" value={bgColor} onChange={handleBgColorChange} title="Highlight color" />

        <button onClick={() => applyFormat('bold')} className="px-2 py-1 border rounded font-bold">B</button>
        <button onClick={() => applyFormat('italic')} className="px-2 py-1 border rounded italic">I</button>
        <button onClick={() => applyFormat('underline')} className="px-2 py-1 border rounded underline">U</button>
        <button onClick={() => applyFormat('insertUnorderedList')} className="px-2 py-1 border rounded">• List</button>
        <button onClick={() => applyFormat('insertOrderedList')} className="px-2 py-1 border rounded">1. List</button>
        <button onClick={() => applyFormat('removeFormat')} className="px-2 py-1 border rounded text-red-600">Clear</button>
      </div>

      <div
        ref={editorRef}
        onInput={handleInput}
        className="min-h-[200px] border rounded p-3 bg-white overflow-auto"
        contentEditable
        suppressContentEditableWarning
        spellCheck={true}
        style={{ fontFamily: selectedFont }}
      />
    </div>
  );
}
