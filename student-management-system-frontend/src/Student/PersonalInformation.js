import React, { useState, useEffect } from 'react';
import { Layout, Card, Button, message, Row, Col, Avatar, Typography } from 'antd';
import { ReloadOutlined, UserOutlined, MailOutlined, BookOutlined, TeamOutlined, CalendarOutlined } from '@ant-design/icons';
import './Contents.css';
import { useNavigate } from 'react-router-dom';
import api from '../api';

const { Content } = Layout;
const { Title, Text } = Typography;

const PersonalInformation = () => {
  const navigate = useNavigate();
  const [student, setStudent] = useState(null);
  const [loading, setLoading] = useState(false);

  const fetchStudentData = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/student/detail');
      setStudent(response.data.data);
    } catch (error) {
      message.error('Failed to fetch personal information.');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const handleAuthError = (event) => {
      message.error(event.detail.message);
      navigate('/login');
    };

    window.addEventListener('auth-error', handleAuthError);

    fetchStudentData();

    return () => {
      window.removeEventListener('auth-error', handleAuthError);
    };
  }, [navigate]);

  const InfoItem = ({ icon, label, value, span = 12 }) => (
    <Col span={span}>
      <Card bordered={false} style={{ height: '100%' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div style={{ 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center',
            width: '32px', 
            height: '32px', 
            borderRadius: '6px',
            background: '#f0f5ff'
          }}>
            {icon}
          </div>
          <div>
            <Text type="secondary" style={{ fontSize: '13px' }}>{label}</Text>
            <div style={{ 
              fontSize: '14px', 
              fontWeight: '500',
              marginTop: '2px',
              color: value ? '#000000d9' : '#00000040'
            }}>
              {value || 'Not Available'}
            </div>
          </div>
        </div>
      </Card>
    </Col>
  );

  if (!student) {
    return (
      <Content className="student-record">
        <div style={{ marginBottom: 12, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0 }}>Personal Information</Title>
          <Button icon={<ReloadOutlined />} onClick={fetchStudentData} loading={loading} />
        </div>
        <Card loading={true} />
      </Content>
    );
  }

  return (
    <Content className="student-record">
      <div style={{ marginBottom: 12, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Title level={4} style={{ margin: 0 }}>Personal Information</Title>
        <Button icon={<ReloadOutlined />} onClick={fetchStudentData} loading={loading} />
      </div>

      {/* Profile Header */}
      <Card style={{ marginBottom: 20 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
          <Avatar 
            size={64} 
            icon={<UserOutlined />} 
            style={{ 
              backgroundColor: '#1890ff',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          />
          <div>
            <Title level={4} style={{ margin: 0 }}>
              {student.firstName} {student.lastName}
            </Title>
            <Text type="secondary" style={{ fontSize: '14px' }}>
              {student.username}
            </Text>
          </div>
        </div>
      </Card>

      {/* Basic Information */}
      <Title level={5} style={{ marginBottom: 12, marginTop: 0 }}>Basic Information</Title>
      <Row gutter={[12, 12]} style={{ marginBottom: 20 }}>
        <InfoItem 
          icon={<MailOutlined style={{ color: '#1890ff', fontSize: '16px' }} />}
          label="Email"
          value={student.email}
          span={24}
        />
        <InfoItem 
          icon={<CalendarOutlined style={{ color: '#52c41a', fontSize: '16px' }} />}
          label="Date of Birth"
          value={student.birthDate}
        />
        <InfoItem 
          icon={<UserOutlined style={{ color: '#722ed1', fontSize: '16px' }} />}
          label="Student ID"
          value={student.id}
        />
      </Row>

      {/* Academic Information */}
      <Title level={5} style={{ marginBottom: 12, marginTop: 0 }}>Academic Information</Title>
      <Row gutter={[12, 12]}>
        <InfoItem 
          icon={<BookOutlined style={{ color: '#eb2f96', fontSize: '16px' }} />}
          label="Programme of Study"
          value={student.programOfStudy}
          span={24}
        />
        <InfoItem 
          icon={<TeamOutlined style={{ color: '#fa8c16', fontSize: '16px' }} />}
          label="Department"
          value={student.department}
        />
        <InfoItem 
          icon={<CalendarOutlined style={{ color: '#13c2c2', fontSize: '16px' }} />}
          label="Expected Graduation"
          value={student.graduationYear}
        />
      </Row>
    </Content>
  );
};

export default PersonalInformation;
