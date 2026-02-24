import jsPDF from 'jspdf';
import 'jspdf-autotable';

const calculateUKGrade = (score) => {
  if (score >= 70) return 'A';
  if (score >= 60) return 'B';
  if (score >= 50) return 'C';
  if (score >= 40) return 'D';
  return 'F';
};

export const generateTranscript = (studentId, records, stats, personalInfo) => {
  const doc = new jsPDF();
  const pageWidth = doc.internal.pageSize.width;
  const margin = 20;

  // Format student ID to ensure it's displayed correctly
  const formattedStudentId = personalInfo?.id?.toString() || studentId?.toString() || '-';

  // Format filename to be more readable and include student name
  const fileName = `transcript_${formattedStudentId}_${personalInfo?.firstName || ''}_${personalInfo?.lastName || ''}_${new Date().toISOString().split('T')[0]}`.replace(/\s+/g, '_');

  // Add university header
  doc.setFontSize(24);
  doc.setTextColor(41, 128, 185);
  doc.text('Awesome University', pageWidth / 2, 30, { align: 'center' });

  doc.setFontSize(18);
  doc.setTextColor(0);
  doc.text('Official Academic Transcript', pageWidth / 2, 45, { align: 'center' });

  // Add horizontal line
  doc.setDrawColor(41, 128, 185);
  doc.setLineWidth(0.5);
  doc.line(margin, 50, pageWidth - margin, 50);

  // Student Information Section
  doc.setFontSize(14);
  doc.setTextColor(41, 128, 185);
  doc.text('Student Information', margin, 65);

  doc.setFontSize(11);
  doc.setTextColor(0);
  const studentInfo = {
    'Student ID': formattedStudentId,
    'Name': `${personalInfo?.firstName || ''} ${personalInfo?.lastName || ''}`,
    'Programme': personalInfo?.programOfStudy?.toString() || '-',
    'Department': personalInfo?.department?.toString() || '-',
    'Expected Graduation': personalInfo?.graduationYear?.toString() || '-'
  };

  let yPos = 75;
  Object.entries(studentInfo).forEach(([key, value]) => {
    doc.setFont(undefined, 'bold');
    doc.text(`${key}:`, margin, yPos);
    doc.setFont(undefined, 'normal');
    doc.text(value?.toString() || '-', margin + 50, yPos);
    yPos += 10;
  });

  // Academic Summary Section
  yPos += 10;
  doc.setFontSize(14);
  doc.setTextColor(41, 128, 185);
  doc.text('Academic Summary', margin, yPos);

  doc.setFontSize(11);
  doc.setTextColor(0);
  const summaryInfo = {
    'Overall Average': `${stats.averageScore?.toString() || '0'}%`,
    'Pass Rate': `${stats.passRate?.toString() || '0'}%`,
    'Total Credits Completed': records.reduce((sum, r) => r.score >= 40 ? sum + (r.credits || 0) : sum, 0).toString(),
    'Total Modules Completed': records.filter(r => r.score >= 40).length.toString()
  };

  yPos += 10;
  Object.entries(summaryInfo).forEach(([key, value]) => {
    doc.setFont(undefined, 'bold');
    doc.text(`${key}:`, margin, yPos);
    doc.setFont(undefined, 'normal');
    doc.text(value?.toString() || '-', margin + 50, yPos);
    yPos += 10;
  });

  // Module Records Table
  yPos += 10;
  doc.setFontSize(14);
  doc.setTextColor(41, 128, 185);
  doc.text('Module Records', margin, yPos);

  const tableData = records.map(record => [
    record.moduleCode?.toString() || '-',
    record.moduleName?.toString() || '-',
    record.date?.toString() || '-',
    `${record.credits?.toString() || '-'}`,
    `${record.score?.toString() || '0'}%`,
    calculateUKGrade(record.score)?.toString() || '-'
  ]);

  doc.autoTable({
    startY: yPos + 5,
    head: [['Module Code', 'Module Name', 'Date', 'Credits', 'Score', 'Grade']],
    body: tableData,
    theme: 'grid',
    headStyles: {
      fillColor: [41, 128, 185],
      textColor: 255,
      fontSize: 10,
      fontStyle: 'bold',
      halign: 'center'
    },
    styles: {
      fontSize: 9,
      cellPadding: 3,
      halign: 'center'
    },
    columnStyles: {
      0: { cellWidth: 25 },
      1: { cellWidth: 60, halign: 'left' },
      2: { cellWidth: 25 },
      3: { cellWidth: 20 },
      4: { cellWidth: 20 },
      5: { cellWidth: 20 }
    },
    alternateRowStyles: {
      fillColor: [245, 245, 245]
    },
    margin: { left: margin, right: margin }
  });

  // Add footer to each page
  const pageCount = doc.internal.getNumberOfPages();
  for(let i = 1; i <= pageCount; i++) {
    doc.setPage(i);
    doc.setFontSize(8);
    doc.setTextColor(128);

    // Add footer text
    doc.text(
      'This is an official transcript from Awesome University',
      pageWidth / 2,
      doc.internal.pageSize.height - 20,
      { align: 'center' }
    );
    doc.text(
      `Generated on ${new Date().toLocaleDateString()}`,
      pageWidth / 2,
      doc.internal.pageSize.height - 15,
      { align: 'center' }
    );
    doc.text(
      `Page ${i} of ${pageCount}`,
      pageWidth / 2,
      doc.internal.pageSize.height - 10,
      { align: 'center' }
    );
  }

  // Save the PDF with the improved filename
  doc.save(`${fileName}.pdf`);
};
