import React, { useState, useEffect } from 'react';
import { Layout, Table, Button, Select, message, Card, Row, Col, Progress } from 'antd';
import './Contents.css';
import api from '../api';
import { ReloadOutlined, DownloadOutlined } from '@ant-design/icons';
import { generateTranscript } from '../utils/transcriptGenerator';

const { Content } = Layout;
const { Option } = Select;

const calculateUKGrade = (score) => {
  if (score >= 70) return 'A';
  if (score >= 60) return 'B';
  if (score >= 50) return 'C';
  if (score >= 40) return 'D';
  return 'F';
};

const AcademicRecords = () => {
  const [selectedYear, setSelectedYear] = useState(null);
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(false);
  const studentId = localStorage.getItem('userId');
  const [personalInfo, setPersonalInfo] = useState(null);

  const fetchRecords = async () => {
    setLoading(true);
    try {
      const registrationsResponse = await api.get('/api/student/get-registrations', {
        params: {
          current: 1,
          size: 100
        }
      });

      if (registrationsResponse.data?.code === 200 && registrationsResponse.data.data?.records) {
        const records = await Promise.all(registrationsResponse.data.data.records.map(async registration => {
          try {
            const moduleResponse = await api.get(`/api/module/${registration.moduleCode}`);
            const moduleData = moduleResponse.data?.data || {};

            return {
              key: registration.id,
              moduleCode: registration.moduleCode,
              moduleName: registration.moduleName,
              credits: moduleData.credits || 0,
              mnc: moduleData.mnc,
              staffFirstName: moduleData.staffFirstName,
              staffLastName: moduleData.staffLastName,
              date: registration.examDate,
              score: registration.score,
              grade: calculateUKGrade(registration.score),
              year: registration.examDate ? new Date(registration.examDate).getFullYear().toString() : '',
              registrationTime: registration.registrationTime
            };
          } catch (error) {
            console.error(`Error fetching module details for ${registration.moduleCode}:`, error);
            return {
              key: registration.id,
              moduleCode: registration.moduleCode,
              moduleName: registration.moduleName,
              credits: 0,
              date: registration.examDate,
              score: registration.score,
              grade: calculateUKGrade(registration.score),
              year: registration.examDate ? new Date(registration.examDate).getFullYear().toString() : '',
              registrationTime: registration.registrationTime
            };
          }
        }));

        console.log('Fetched records with module details:', records);
        setRecords(records);
      } else {
        console.log('No records found in response:', registrationsResponse);
      }
    } catch (error) {
      console.error('Error fetching records:', error);
      message.error('Failed to load academic records');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    console.log('Current studentId:', studentId);
    fetchRecords();
  }, [studentId]);

  // Get unique years from records
  const years = [...new Set(records.map(record => record.year))].sort();

  // Filter records based on selected year
  const filteredRecords = selectedYear
    ? records.filter(record => record.year === selectedYear)
    : records;

  const columns = [
    {
      title: 'Module Code',
      dataIndex: 'moduleCode',
      key: 'moduleCode',
      width: 120,
      render: text => text || '-'
    },
    {
      title: 'Module Name',
      dataIndex: 'moduleName',
      key: 'moduleName',
      width: 250,
      render: text => text || '-'
    },
    {
      title: 'Credits',
      dataIndex: 'credits',
      key: 'credits',
      width: 100,
      render: credits => credits || 0,
      sorter: (a, b) => (a.credits || 0) - (b.credits || 0)
    },
    {
      title: 'Mandatory Non-Condonable',
      dataIndex: 'mnc',
      key: 'mnc',
      width: 180,
      render: mnc => mnc ? 'Yes' : 'No'
    },
    {
      title: 'Module Leader',
      key: 'moduleLeader',
      width: 180,
      render: (_, record) => `${record.staffFirstName || ''} ${record.staffLastName || ''}`.trim() || '-'
    },
    {
      title: 'Exam Date',
      dataIndex: 'date',
      key: 'date',
      width: 120,
      render: date => date ? new Date(date).toLocaleDateString() : '-',
      sorter: (a, b) => new Date(a.date || 0) - new Date(b.date || 0)
    },
    {
      title: 'Score',
      dataIndex: 'score',
      key: 'score',
      width: 100,
      render: score => score !== null && score !== undefined ? score : '-',
      sorter: (a, b) => (a.score || 0) - (b.score || 0)
    },
    {
      title: 'Grade',
      dataIndex: 'grade',
      key: 'grade',
      width: 100,
      render: grade => grade || '-'
    }
  ];

  // Add statistics calculation
  const calculateOverallStatistics = (records) => {
    if (!records || records.length === 0) return null;

    const validScores = records.filter(record => record.score !== null && record.score !== undefined);
    const scores = validScores.map(record => record.score);
    const passedModules = scores.filter(score => score >= 40).length;

    // Calculate grade distribution
    const gradeDistribution = records.reduce((acc, record) => {
      const grade = calculateUKGrade(record.score);
      acc[grade] = (acc[grade] || 0) + 1;
      return acc;
    }, { A: 0, B: 0, C: 0, D: 0, F: 0 });

    // Calculate median
    const sortedScores = [...scores].sort((a, b) => a - b);
    const median = sortedScores.length % 2 === 0
      ? (sortedScores[sortedScores.length / 2 - 1] + sortedScores[sortedScores.length / 2]) / 2
      : sortedScores[Math.floor(sortedScores.length / 2)];

    // Calculate standard deviation
    const mean = scores.reduce((a, b) => a + b, 0) / scores.length;
    const standardDeviation = Math.sqrt(
      scores.reduce((sq, n) => sq + Math.pow(n - mean, 2), 0) / scores.length
    );

    return {
      totalModules: records.length,
      completedModules: validScores.length,
      averageScore: (scores.reduce((a, b) => a + b, 0) / scores.length).toFixed(2),
      passRate: ((passedModules / validScores.length) * 100).toFixed(1),
      medianScore: median.toFixed(2),
      standardDeviation: standardDeviation.toFixed(2),
      highestScore: Math.max(...scores),
      lowestScore: Math.min(...scores),
      gradeDistribution
    };
  };

  // Add function to fetch student info
  const fetchPersonalInfo = async () => {
    try {
      const response = await api.get('/api/student/detail');
      if (response.data?.code === 200) {
        setPersonalInfo(response.data.data);
      }
    } catch (error) {
      console.error('Failed to fetch student info:', error);
      message.error('Failed to fetch student information');
    }
  };

  useEffect(() => {
    fetchPersonalInfo();
  }, []);

  const handleGenerateTranscript = () => {
    if (!personalInfo) {
      message.error('Unable to generate transcript: Missing student information');
      return;
    }
    const stats = calculateOverallStatistics(records);
    generateTranscript(localStorage.getItem('userId'), records, stats, personalInfo);
  };

  return (
    <Content className="student-record">
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Academic Records</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button
            icon={<DownloadOutlined />}
            onClick={handleGenerateTranscript}
            type="primary"
          >
            Download Transcript
          </Button>
          <Button icon={<ReloadOutlined />} onClick={fetchRecords} loading={loading} />
          <Select
            placeholder="Filter by Year"
            onChange={setSelectedYear}
            allowClear
            style={{ width: 200 }}
            value={selectedYear}
          >
            <Option value={null}>Show All</Option>
            {years.map((year) => (
              <Option key={year} value={year}>{year}</Option>
            ))}
          </Select>
        </div>
      </div>

      {records.length > 0 && calculateOverallStatistics(records) && (
        <Card title="Academic Performance Overview" style={{ marginBottom: 24 }}>
          {/* Key Performance Metrics */}
          <Row gutter={[16, 16]}>
            <Col span={8}>
              <Card bordered={false} style={{ background: '#f0f5ff' }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#1890ff' }}>
                    {calculateOverallStatistics(records).averageScore}%
                  </div>
                  <div style={{ fontSize: '14px', color: '#666' }}>Overall Average</div>
                </div>
              </Card>
            </Col>
            <Col span={8}>
              <Card bordered={false} style={{ background: '#f6ffed' }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#52c41a' }}>
                    {calculateOverallStatistics(records).passRate}%
                  </div>
                  <div style={{ fontSize: '14px', color: '#666' }}>Pass Rate</div>
                </div>
              </Card>
            </Col>
            <Col span={8}>
              <Card bordered={false} style={{ background: '#f0f2ff' }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#722ed1' }}>
                    {calculateOverallStatistics(records).medianScore}%
                  </div>
                  <div style={{ fontSize: '14px', color: '#666' }}>Median Score</div>
                </div>
              </Card>
            </Col>
          </Row>

          {/* Grade Distribution and Score Analysis */}
          <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
            <Col span={16}>
              <div style={{ background: '#fafafa', padding: '16px', borderRadius: '8px', height: '100%' }}>
                <div style={{ marginBottom: 16, fontSize: '14px', fontWeight: 'bold' }}>Grade Distribution</div>
                {Object.entries(calculateOverallStatistics(records).gradeDistribution).map(([grade, count]) => {
                  const percentage = (count / records.length) * 100;
                  const colors = {
                    A: '#52c41a',
                    B: '#1890ff',
                    C: '#722ed1',
                    D: '#faad14',
                    F: '#f5222d'
                  };
                  return (
                    <div key={grade} style={{ marginBottom: 8 }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                        <span style={{ color: colors[grade], fontWeight: 'bold' }}>
                          Grade {grade}
                        </span>
                        <span>{count} modules ({percentage.toFixed(1)}%)</span>
                      </div>
                      <Progress
                        percent={percentage}
                        strokeColor={colors[grade]}
                        showInfo={false}
                        size="small"
                        strokeWidth={4}
                      />
                    </div>
                  );
                })}
              </div>
            </Col>
            <Col span={8}>
              <div style={{ background: '#fafafa', padding: '16px', borderRadius: '8px', height: '100%' }}>
                <div style={{ marginBottom: 16, fontSize: '14px', fontWeight: 'bold' }}>Score Analysis</div>
                <div style={{ textAlign: 'center', marginBottom: 16 }}>
                  <div style={{ fontSize: '24px', color: '#52c41a', fontWeight: 'bold' }}>
                    {calculateOverallStatistics(records).highestScore}%
                  </div>
                  <div style={{ fontSize: '13px', color: '#666' }}>Highest Score</div>
                </div>
                <div style={{ textAlign: 'center', marginBottom: 16 }}>
                  <div style={{ fontSize: '24px', color: '#f5222d', fontWeight: 'bold' }}>
                    {calculateOverallStatistics(records).lowestScore}%
                  </div>
                  <div style={{ fontSize: '13px', color: '#666' }}>Lowest Score</div>
                </div>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontSize: '24px', color: '#1890ff', fontWeight: 'bold' }}>
                    ±{calculateOverallStatistics(records).standardDeviation}
                  </div>
                  <div style={{ fontSize: '13px', color: '#666' }}>Standard Deviation</div>
                </div>
              </div>
            </Col>
          </Row>
        </Card>
      )}

      <Table
        columns={columns}
        dataSource={filteredRecords}
        pagination={false}
        className="exam-record-table"
        loading={loading}
      />
    </Content>
  );
};

export default AcademicRecords;
