// --- src/components/FlashcardsPanel.tsx ---
import React from 'react';

interface Props {
    flashcards: string[][];
    currentCard: number;
    setCurrentCard: (n: number) => void;
    showAnswer: boolean;
    setShowAnswer: (v: boolean) => void;
    loading: boolean;
}

export default function FlashcardsPanel({ flashcards, currentCard, setCurrentCard, showAnswer, setShowAnswer, loading }: Props) {
    const total = flashcards.length;

    if (loading) {
        return (
            <div className="max-w-xl mx-auto mt-10">
                <div className="animate-pulse space-y-4">
                    <div className="h-6 bg-gray-300 rounded w-1/3"></div>
                    <div className="h-20 bg-gray-200 rounded"></div>
                    <div className="flex justify-between">
                        <div className="h-10 w-24 bg-gray-300 rounded"></div>
                        <div className="h-10 w-24 bg-gray-300 rounded"></div>
                    </div>
                </div>
            </div>
        );
    }

    if (!flashcards[currentCard]) {
        <p>No flashcards available.</p>
        return null; // Or a fallback UI like <p>No flashcards available.</p>
    }


    return (
        <div className="max-w-xl mx-auto mt-10 space-y-4">
            <h2 className="text-2xl font-bold text-purple-700 flex items-center gap-2">🧠 Flashcards</h2>

            <div className="border p-6 rounded-lg bg-gray-50 shadow-md text-left transition duration-300 ease-in-out">
                <p className="font-semibold text-lg text-gray-800 mb-4">
                    Q{currentCard + 1}: {flashcards[currentCard][0]}
                </p>

                {showAnswer ? (
                    <p className="text-green-700 bg-green-50 px-4 py-3 rounded-md">
                        {flashcards[currentCard][1]}
                    </p>
                ) : (
                    <button
                        onClick={() => setShowAnswer(true)}
                        className="text-blue-600 underline text-sm"
                    >
                        Show Answer
                    </button>
                )}
            </div>

            <div className="flex justify-between pt-4">
                <button
                    onClick={() => {
                        setCurrentCard(Math.max(currentCard - 1, 0));
                        setShowAnswer(false);
                    }}
                    disabled={currentCard === 0}
                    className="px-4 py-2 bg-blue-500 text-white rounded disabled:opacity-50"
                >⬅ Previous</button>
                <span className="text-sm text-gray-600">Card {currentCard + 1} of {total}</span>
                <button
                    onClick={() => {
                        setCurrentCard(Math.min(currentCard + 1, total - 1));
                        setShowAnswer(false);
                    }}
                    disabled={currentCard === total - 1}
                    className="px-4 py-2 bg-blue-500 text-white rounded disabled:opacity-50"
                >Next ➡</button>
            </div>
        </div>
    );
}
