// ✅ BACKEND: index.js
import dotenv from 'dotenv';
dotenv.config();

import express from 'express';
import cors from 'cors';
import multer from 'multer';
import { exec } from 'child_process';
import fs from 'fs';
import path from 'path';
import fetch from 'node-fetch';
import pdfjs from 'pdfjs-dist/legacy/build/pdf.js';

const { getDocument, GlobalWorkerOptions } = pdfjs;
GlobalWorkerOptions.workerSrc = 'pdfjs-dist/legacy/build/pdf.worker.js';

const app = express();
const port = 4000;

app.use(cors());
app.use(express.json());
app.use(express.static('converted'));

const upload = multer({ dest: 'uploads/' });

app.post('/upload', upload.single('file'), (req, res) => {
  const file = req.file;
  if (!file) return res.status(400).json({ error: 'No file uploaded.' });

  const fileExt = path.extname(file.originalname).toLowerCase();
  const outputFileName = `${file.filename}.pdf`;
  const outputPath = path.join('converted', outputFileName);

  if (fileExt === '.pdf') {
    fs.rename(file.path, outputPath, (err) => {
      if (err) return res.status(500).json({ error: 'Failed to move PDF file.' });
      const pdfUrl = `http://localhost:${port}/${outputFileName}`;
      res.json({ pdfUrl, pdfPath: outputPath });
    });
  } else {
    const command = `soffice --headless --convert-to pdf --outdir converted ${file.path}`;
    exec(command, (err) => {
      if (err) return res.status(500).json({ error: 'PDF conversion failed.' });
      const pdfUrl = `http://localhost:${port}/${outputFileName}`;
      res.json({ pdfUrl, pdfPath: outputPath });
    });
  }
});

app.post('/generate-flashcards', async (req, res) => {
  const { pdfPath, pageNumber, questionCount } = req.body;

  if (!pdfPath || !pageNumber) return res.status(400).json({ error: 'Missing data.' });

  const filename = path.basename(pdfPath);
  const absolutePath = path.resolve('converted', filename);
  if (!fs.existsSync(absolutePath)) return res.status(404).json({ error: 'PDF not found.' });

  try {
    const loadingTask = getDocument(absolutePath);
    const pdfDoc = await loadingTask.promise;
    const page = await pdfDoc.getPage(pageNumber);
    const content = await page.getTextContent();
    const pageText = content.items.map((item) => item.str).join(' ');

    const ollamaRes = await fetch('http://localhost:11434/api/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        model: 'gemma:2b',
        messages: [
          {
            role: 'system',
            content: `You are a veterinary tutor. Generate exactly ${questionCount || 3} flashcards based on the provided material.

            Each flashcard must be in this format:

            Q1: [question here]
            A1: [answer here]

            Q2: ...
            A2: ...
            Q3: ...
            A3: ...

            Only return numbered flashcards. Do NOT explain anything. Do NOT include anything before or after the list.`

          },
          {
            role: 'user',
            content: `Generate ${questionCount || 3} veterinary flashcards based only on this content:\n\n${pageText}`
          }
        ],
        stream: false,
      }),
    });

    const json = await ollamaRes.json();
    const raw = json.message?.content || '';

    console.log('[🧠 Raw LLM Output]\n', raw);

    // --- Clean parsing ---
    const lines = raw.split('\n').map(line => line.trim()).filter(Boolean);
    const flashcards = [];
    let currentQuestion = null;

    for (const line of lines) {
      if (/^Q\d*:/i.test(line)) {
        currentQuestion = line.replace(/^Q\d*:/i, '').trim();
      } else if (/^A\d*:/i.test(line) && currentQuestion) {
        const answer = line.replace(/^A\d*:/i, '').trim();
        if (answer.length > 0) {
          flashcards.push([currentQuestion, answer]);
        }
        currentQuestion = null;
      }
    }


    console.log('[✅ Parsed Flashcards]', flashcards);

    res.json({ flashcards });
  } catch (err) {
    console.error('❌ Flashcard generation error:', err);
    res.status(500).json({ error: 'Flashcard generation failed.' });
  }
});


app.listen(port, () => {
  console.log(`✅ Server running at http://localhost:${port}`);
});
