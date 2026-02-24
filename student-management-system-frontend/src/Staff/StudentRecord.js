import React, {useState, useEffect, useCallback} from 'react';
import { Layout, Table, Button, Modal, Form, Input, message, Select, Descriptions, Card, Row, Col, Progress, Statistic } from 'antd';
import { ReloadOutlined, EditOutlined, DeleteOutlined, DownloadOutlined } from '@ant-design/icons';
import './Contents.css';
import api from "../api";
import { generateTranscript } from '../utils/transcriptGenerator';

const { Content } = Layout;

// Add calculateUKGrade function at the top
const calculateUKGrade = (score) => {
  if (score >= 70) return 'A';
  if (score >= 60) return 'B';
  if (score >= 50) return 'C';
  if (score >= 40) return 'D';
  return 'F';
};

const StudentRecord = ({ studentId, setSelectedKey, readOnly = false }) => {
  const [student, setStudent] = useState(null);
  const [loading, setLoading] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editForm] = Form.useForm();
  const [examRecords, setExamRecords] = useState([]);
  const [isDeleteModalVisible, setIsDeleteModalVisible] = useState(false);

  const fetchStudentData = useCallback(async () => {
    if (!studentId) return;
    setLoading(true);
    try {
      const response = await api.get(`/api/student/${studentId}`);
      setStudent(response.data.data);

      const recordsResponse = await api.get('/api/registration', {
        params: {
          studentId: studentId,
          current: 1,
          size: 100
        }
      });
      setExamRecords(recordsResponse.data?.data?.records || []);
    } catch (error) {
      message.error('Failed to fetch student data');
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, [studentId]);

  useEffect(() => {
    fetchStudentData();
  }, [studentId, fetchStudentData]);

  const handleEditSubmit = async () => {
    try {
      const values = await editForm.validateFields();
      await api.put(`/api/student/edit/${studentId}`, {
        ...values,
        graduationYear: parseInt(values.graduationYear)
      });

      message.success('Student details updated successfully');
      setIsEditModalOpen(false);
      await fetchStudentData();
    } catch (error) {
      console.error('Error updating student:', error);
      message.error(error.response?.data?.message || 'Failed to update student details');
    }
  };

  const handleDeleteConfirm = async () => {
    try {
      await api.delete(`/api/student/delete/${studentId}`);
      message.success('Student deleted successfully');
      setIsDeleteModalVisible(false);
      setSelectedKey('2');
    } catch (error) {
      console.error('Error deleting student:', error);
      message.error(error.response?.data?.message || 'Failed to delete student');
    }
  };

  // Update calculateStatistics function
  const calculateStatistics = () => {
    if (!examRecords.length) return null;

    const scores = examRecords.map(record => record.score);
    const passedExams = scores.filter(score => score >= 40).length;

    // Calculate grade distribution
    const gradeDistribution = examRecords.reduce((acc, record) => {
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
      totalExams: examRecords.length,
      averageScore: (scores.reduce((a, b) => a + b, 0) / scores.length).toFixed(2),
      passRate: ((passedExams / examRecords.length) * 100).toFixed(1),
      highestScore: Math.max(...scores),
      lowestScore: Math.min(...scores),
      medianScore: median.toFixed(2),
      standardDeviation: standardDeviation.toFixed(2),
      gradeDistribution
    };
  };

  // Update examinationColumns to include date
  const examinationColumns = [
    {
      title: 'Module Code',
      dataIndex: 'moduleCode',
      key: 'moduleCode',
      sorter: (a, b) => a.moduleCode.localeCompare(b.moduleCode),
      width: 120
    },
    {
      title: 'Module Name',
      dataIndex: 'moduleName',
      key: 'moduleName',
      sorter: (a, b) => a.moduleName.localeCompare(b.moduleName),
      width: 250
    },
    {
      title: 'Exam Date',
      dataIndex: 'examDate',
      key: 'examDate',
      sorter: (a, b) => new Date(a.examDate) - new Date(b.examDate),
      render: (date) => date ? new Date(date).toLocaleDateString() : '-',
      width: 120
    },
    {
      title: 'Score',
      dataIndex: 'score',
      key: 'score',
      sorter: (a, b) => a.score - b.score,
      render: (score) => `${score}%`,
      width: 100
    },
    {
      title: 'Grade',
      key: 'grade',
      render: (_, record) => calculateUKGrade(record.score),
      width: 100
    }
  ];

  const formItems = [
    { name: 'firstName', label: 'First Name', rules: [{ required: true }] },
    { name: 'lastName', label: 'Last Name', rules: [{ required: true }] },
    { name: 'username', label: 'Username', rules: [{ required: true }] },
    {
      name: 'password',
      label: 'Password',
      rules: [{ required: false }],
      inputType: 'password',
      placeholder: 'Leave blank to keep current password'
    },
    {
      name: 'email',
      label: 'Email',
      rules: [
        { required: true },
        { type: 'email', message: 'Please enter a valid email' }
      ]
    },
    { name: 'programOfStudy', label: 'Programme of Study', rules: [{ required: true }] },
    { name: 'department', label: 'Department', rules: [{ required: true }] },
    {
      name: 'graduationYear',
      label: 'Graduation Year',
      rules: [{ required: true }],
      inputType: 'select',
      options: Array.from({ length: 10 }, (_, i) => {
        const year = new Date().getFullYear() + i;
        return { value: year, label: year.toString() };
      })
    },
    {
      name: 'birthDate',
      label: 'Date of Birth',
      rules: [{ required: true }],
      placeholder: 'YYYY-MM-DD'
    }
  ];

  // Add this helper function for email rendering
  const renderEmail = (email) => {
    return email ? (
      <a href={`mailto:${email}`} style={{ color: '#1890ff' }}>
        {email}
      </a>
    ) : '-';
  };

  // Add function to handle transcript generation
  const handleGenerateTranscript = () => {
    if (!student) {
      message.error('Unable to generate transcript: Missing student information');
      return;
    }
    const stats = calculateStatistics();
    generateTranscript(studentId, examRecords, stats, student);
  };

  return (
    <Content style={{ margin: '24px 16px 0', padding: 24, background: '#fff' }}>
      {/* Header Section */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Student Details</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button icon={<ReloadOutlined />} onClick={fetchStudentData} loading={loading} />
          {examRecords.length > 0 && (
            <Button
              type="primary"
              icon={<DownloadOutlined />}
              onClick={handleGenerateTranscript}
            >
              Download Transcript
            </Button>
          )}
          {!readOnly && (
            <>
              <Button
                type="primary"
                icon={<EditOutlined />}
                onClick={() => {
                  editForm.setFieldsValue(student);
                  setIsEditModalOpen(true);
                }}
              >
                Edit Detail
              </Button>
              <Button
                danger
                icon={<DeleteOutlined />}
                onClick={() => setIsDeleteModalVisible(true)}
              >
                Delete Student
              </Button>
            </>
          )}
        </div>
      </div>

      {/* Student Information */}
      <Descriptions bordered column={2} style={{ marginBottom: 24 }}>
        <Descriptions.Item label="First Name">{student?.firstName}</Descriptions.Item>
        <Descriptions.Item label="Last Name">{student?.lastName}</Descriptions.Item>
        <Descriptions.Item label="Username">{student?.username}</Descriptions.Item>
        <Descriptions.Item label="Email">{renderEmail(student?.email)}</Descriptions.Item>
        <Descriptions.Item label="Programme of Study">{student?.programOfStudy}</Descriptions.Item>
        <Descriptions.Item label="Department">{student?.department}</Descriptions.Item>
        <Descriptions.Item label="Graduation Year">{student?.graduationYear}</Descriptions.Item>
        <Descriptions.Item label="Date of Birth">{student?.birthDate}</Descriptions.Item>
      </Descriptions>

      {/* Academic Performance Statistics */}
      {examRecords.length > 0 && (
        <Card title="Academic Performance Overview" style={{ marginBottom: 24 }}>
          <Row gutter={[16, 16]}>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f0f5ff' }}>
                <Statistic
                  title="Average Score"
                  value={calculateStatistics().averageScore}
                  suffix="%"
                  precision={1}
                  valueStyle={{ color: '#1890ff' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f6ffed' }}>
                <Statistic
                  title="Pass Rate"
                  value={calculateStatistics().passRate}
                  suffix="%"
                  precision={1}
                  valueStyle={{ color: '#52c41a' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#fff0f6' }}>
                <Statistic
                  title="Total Exams"
                  value={calculateStatistics().totalExams}
                  valueStyle={{ color: '#eb2f96' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f0f2ff' }}>
                <Statistic
                  title="Score Range"
                  value={`${calculateStatistics().lowestScore}-${calculateStatistics().highestScore}`}
                  suffix="%"
                  valueStyle={{ color: '#722ed1' }}
                />
              </Card>
            </Col>
          </Row>

          {/* Grade Distribution */}
          <div style={{ marginTop: 24 }}>
            <h4>Grade Distribution</h4>
            <Row gutter={[16, 16]}>
              {Object.entries(calculateStatistics().gradeDistribution).map(([grade, count]) => {
                const percentage = (count / examRecords.length) * 100;
                const colors = {
                  A: '#52c41a',
                  B: '#1890ff',
                  C: '#722ed1',
                  D: '#faad14',
                  F: '#f5222d'
                };
                return (
                  <Col span={24} key={grade}>
                    <div style={{ marginBottom: 8 }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                        <span style={{ color: colors[grade], fontWeight: 'bold' }}>
                          Grade {grade}
                        </span>
                        <span>{count} exams ({percentage.toFixed(1)}%)</span>
                      </div>
                      <Progress
                        percent={percentage}
                        strokeColor={colors[grade]}
                        showInfo={false}
                        size="small"
                        strokeWidth={4}
                      />
                    </div>
                  </Col>
                );
              })}
            </Row>
          </div>
        </Card>
      )}

      {/* Examination Records Table */}
      <div style={{ marginBottom: 16 }}>
        <h3>Examination Records</h3>
      </div>
      <Table
        columns={examinationColumns}
        dataSource={examRecords}
        pagination={false}
        rowKey="id"
        onRow={(record) => ({
          onClick: () => setSelectedKey(['recordDetail', record.id]),
          style: { cursor: 'pointer' }
        })}
      />

      {/* Only render modals if not in readOnly mode */}
      {!readOnly && (
        <>
          <Modal
            title="Edit Student Details"
            open={isEditModalOpen}
            onOk={handleEditSubmit}
            onCancel={() => setIsEditModalOpen(false)}
            width={600}
          >
            <Form form={editForm} layout="vertical">
              {formItems.map(item => (
                <Form.Item
                  key={item.name}
                  name={item.name}
                  label={item.label}
                  rules={item.rules}
                >
                  {item.inputType === 'password' ? (
                    <Input.Password placeholder={item.placeholder} />
                  ) : item.inputType === 'select' ? (
                    <Select style={{ width: '100%' }}>
                      {item.options.map(option => (
                        <Select.Option key={option.value} value={option.value}>
                          {option.label}
                        </Select.Option>
                      ))}
                    </Select>
                  ) : (
                    <Input placeholder={item.placeholder} />
                  )}
                </Form.Item>
              ))}
            </Form>
          </Modal>

          <Modal
            title="Delete Student"
            open={isDeleteModalVisible}
            onOk={handleDeleteConfirm}
            onCancel={() => setIsDeleteModalVisible(false)}
            okText="Delete"
            okButtonProps={{ danger: true }}
          >
            <p>Are you sure you want to delete this student?</p>
            <p>This will permanently delete:</p>
            <ul>
              <li>Student {student?.id} - {student?.firstName} {student?.lastName}</li>
              <li>All academic records and grades</li>
              <li>All module registrations</li>
              <li>All personal information</li>
            </ul>
            <p style={{ color: '#ff4d4f', marginTop: 16 }}>
              This action cannot be undone.
            </p>
          </Modal>
        </>
      )}
    </Content>
  );
};

export default StudentRecord;
